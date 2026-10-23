package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.BudgetComponentResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import apap.ti._2026.simbg_2406349285_be.models.BudgetComponent;
import apap.ti._2026.simbg_2406349285_be.models.ClaimItem;
import apap.ti._2026.simbg_2406349285_be.repositories.BudgetComponentRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetComponentServiceImpl implements BudgetComponentService {

    private final BudgetComponentRepository budgetComponentRepository;
    private final ClaimItemRepository claimItemRepository;
    private final AnggaranService anggaranService;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetComponentResponse> getComponentsByAnggaranId(String anggaranId) {
        // Validate anggaran existence
        anggaranService.getAnggaranEntityById(anggaranId);
        return budgetComponentRepository.findByAnggaranId(anggaranId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetComponentResponse getComponentById(String anggaranId, String id) {
        BudgetComponent component = getComponentEntityById(id);
        if (!component.getAnggaran().getId().equals(anggaranId)) {
            throw new ResourceNotFoundException("Komponen anggaran tidak ditemukan pada anggaran: " + anggaranId);
        }
        return mapToResponse(component);
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetComponent getComponentEntityById(String id) {
        return budgetComponentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Komponen anggaran tidak ditemukan: " + id));
    }

    @Override
    @Transactional
    public BudgetComponentResponse createComponent(String anggaranId, CreateBudgetComponentRequest request) {
        Anggaran anggaran = anggaranService.getAnggaranEntityById(anggaranId);

        if (request.getAllocatedAmount() == null || request.getAllocatedAmount() <= 0) {
            throw new BadRequestException("Alokasi dana harus lebih dari 0");
        }
        if (request.getMaxRate() == null || request.getMaxRate() <= 0) {
            throw new BadRequestException("Tarif maksimum harus lebih dari 0");
        }

        List<BudgetComponent> existingComponents = budgetComponentRepository.findByAnggaranId(anggaranId);
        long currentTotalAllocated = existingComponents.stream()
                .mapToLong(c -> c.getAllocatedAmount() != null ? c.getAllocatedAmount() : 0L)
                .sum();

        if (currentTotalAllocated + request.getAllocatedAmount() > anggaran.getTotalAnggaran()) {
            throw new BadRequestException("Total alokasi seluruh komponen anggaran melebihi total anggaran");
        }

        String generatedId = generateNextComponentId();
        LocalDateTime now = LocalDateTime.now();

        BudgetComponent component = BudgetComponent.builder()
                .id(generatedId)
                .anggaran(anggaran)
                .componentName(request.getComponentName())
                .allocatedAmount(request.getAllocatedAmount())
                .maxRate(request.getMaxRate())
                .rateBasis(request.getRateBasis())
                .description(request.getDescription())
                .createdAt(now)
                .updatedAt(now)
                .build();

        BudgetComponent saved = budgetComponentRepository.save(component);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public BudgetComponentResponse updateComponent(String anggaranId, String id, UpdateBudgetComponentRequest request) {
        Anggaran anggaran = anggaranService.getAnggaranEntityById(anggaranId);
        BudgetComponent component = getComponentEntityById(id);

        if (!component.getAnggaran().getId().equals(anggaranId)) {
            throw new BadRequestException("Komponen anggaran tidak sesuai dengan anggaran yang ditentukan");
        }

        if (request.getAllocatedAmount() == null || request.getAllocatedAmount() <= 0) {
            throw new BadRequestException("Alokasi dana harus lebih dari 0");
        }
        if (request.getMaxRate() == null || request.getMaxRate() <= 0) {
            throw new BadRequestException("Tarif maksimum harus lebih dari 0");
        }

        List<BudgetComponent> otherComponents = budgetComponentRepository.findByAnggaranId(anggaranId).stream()
                .filter(c -> !c.getId().equals(id))
                .toList();

        long otherTotalAllocated = otherComponents.stream()
                .mapToLong(c -> c.getAllocatedAmount() != null ? c.getAllocatedAmount() : 0L)
                .sum();

        if (otherTotalAllocated + request.getAllocatedAmount() > anggaran.getTotalAnggaran()) {
            throw new BadRequestException("Total alokasi seluruh komponen anggaran melebihi total anggaran");
        }

        long usedAmount = calculateUsedAmount(component.getId());
        if (request.getAllocatedAmount() < usedAmount) {
            throw new BadRequestException("Alokasi dana baru tidak boleh lebih kecil dari total pengeluaran yang sudah digunakan (" + usedAmount + ")");
        }

        component.setComponentName(request.getComponentName());
        component.setAllocatedAmount(request.getAllocatedAmount());
        component.setMaxRate(request.getMaxRate());
        component.setRateBasis(request.getRateBasis());
        component.setDescription(request.getDescription());
        component.setUpdatedAt(LocalDateTime.now());

        BudgetComponent updated = budgetComponentRepository.save(component);
        return mapToResponse(updated);
    }

    private synchronized String generateNextComponentId() {
        List<BudgetComponent> all = budgetComponentRepository.findAll();
        int maxSeq = 0;
        for (BudgetComponent b : all) {
            if (b.getId() != null && b.getId().startsWith("BGC")) {
                try {
                    int seq = Integer.parseInt(b.getId().substring(3));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("BGC%04d", maxSeq + 1);
    }

    private long calculateUsedAmount(String componentId) {
        List<ClaimItem> items = claimItemRepository.findByBudgetComponentId(componentId);
        return items.stream()
                .mapToLong(item -> item.getAmount() != null ? item.getAmount() : 0L)
                .sum();
    }

    private BudgetComponentResponse mapToResponse(BudgetComponent c) {
        long usedAmount = calculateUsedAmount(c.getId());
        long remaining = c.getAllocatedAmount() - usedAmount;

        return BudgetComponentResponse.builder()
                .id(c.getId())
                .componentName(c.getComponentName())
                .allocatedAmount(c.getAllocatedAmount())
                .maxRate(c.getMaxRate())
                .rateBasis(c.getRateBasis())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .anggaranId(c.getAnggaran() != null ? c.getAnggaran().getId() : null)
                .usedAmount(usedAmount)
                .remainingAmount(remaining)
                .build();
    }
}
