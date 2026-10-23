package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.BudgetComponent;
import apap.ti._2026.simbg_2406349285_be.models.Claim;
import apap.ti._2026.simbg_2406349285_be.models.ClaimItem;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimItemRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimItemServiceImpl implements ClaimItemService {

    private final ClaimItemRepository claimItemRepository;
    private final ClaimRepository claimRepository;
    private final ClaimService claimService;
    private final BudgetComponentService budgetComponentService;

    @Override
    @Transactional(readOnly = true)
    public List<ClaimItemResponse> getItemsByClaimId(String claimId) {
        claimService.getClaimEntityById(claimId);
        return claimItemRepository.findByClaimId(claimId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimItemResponse getItemById(String claimId, String id) {
        ClaimItem item = claimItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClaimItem tidak ditemukan: " + id));

        if (!item.getClaim().getId().equals(claimId)) {
            throw new ResourceNotFoundException("ClaimItem tidak ditemukan pada claim: " + claimId);
        }
        return mapToResponse(item);
    }

    @Override
    @Transactional
    public ClaimItemResponse createItem(String claimId, CreateClaimItemRequest request) {
        Claim claim = claimService.getClaimEntityById(claimId);

        if (!"DRAFT".equalsIgnoreCase(claim.getStatus())) {
            throw new BadRequestException("ClaimItem hanya dapat ditambahkan pada claim berstatus DRAFT");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Kuantitas harus lebih dari 0");
        }
        if (request.getUnitPrice() == null || request.getUnitPrice() <= 0) {
            throw new BadRequestException("Harga satuan harus lebih dari 0");
        }

        BudgetComponent component = budgetComponentService.getComponentEntityById(request.getBudgetComponentId());

        if (!component.getAnggaran().getId().equals(claim.getAnggaran().getId())) {
            throw new BadRequestException("Budget Component tidak terdaftar pada anggaran claim ini");
        }

        if (request.getUnitPrice() > component.getMaxRate()) {
            throw new BadRequestException("Harga satuan (" + request.getUnitPrice() + ") melebihi tarif maksimum (" + component.getMaxRate() + ")");
        }

        long newItemAmount = (long) request.getQuantity() * request.getUnitPrice();
        long currentUsedAmount = calculateComponentUsage(component.getId(), null);

        if (currentUsedAmount + newItemAmount > component.getAllocatedAmount()) {
            throw new BadRequestException("Total pengeluaran melebihi alokasi dana komponen anggaran (" + component.getAllocatedAmount() + ")");
        }

        String generatedId = generateNextClaimItemId();
        ClaimItem item = ClaimItem.builder()
                .id(generatedId)
                .claim(claim)
                .budgetComponent(component)
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .amount(newItemAmount)
                .build();

        ClaimItem saved = claimItemRepository.save(item);

        // Recalculate claim total
        recalculateClaimTotal(claim);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ClaimItemResponse updateItem(String claimId, String id, UpdateClaimItemRequest request) {
        Claim claim = claimService.getClaimEntityById(claimId);

        if (!"DRAFT".equalsIgnoreCase(claim.getStatus())) {
            throw new BadRequestException("ClaimItem hanya dapat diubah pada claim berstatus DRAFT");
        }

        ClaimItem item = claimItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClaimItem tidak ditemukan: " + id));

        if (!item.getClaim().getId().equals(claimId)) {
            throw new BadRequestException("ClaimItem tidak sesuai dengan claim yang ditentukan");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Kuantitas harus lebih dari 0");
        }
        if (request.getUnitPrice() == null || request.getUnitPrice() <= 0) {
            throw new BadRequestException("Harga satuan harus lebih dari 0");
        }

        BudgetComponent component = budgetComponentService.getComponentEntityById(request.getBudgetComponentId());

        if (!component.getAnggaran().getId().equals(claim.getAnggaran().getId())) {
            throw new BadRequestException("Budget Component tidak terdaftar pada anggaran claim ini");
        }

        if (request.getUnitPrice() > component.getMaxRate()) {
            throw new BadRequestException("Harga satuan (" + request.getUnitPrice() + ") melebihi tarif maksimum (" + component.getMaxRate() + ")");
        }

        long newItemAmount = (long) request.getQuantity() * request.getUnitPrice();
        long otherUsedAmount = calculateComponentUsage(component.getId(), id);

        if (otherUsedAmount + newItemAmount > component.getAllocatedAmount()) {
            throw new BadRequestException("Total pengeluaran melebihi alokasi dana komponen anggaran (" + component.getAllocatedAmount() + ")");
        }

        item.setBudgetComponent(component);
        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setAmount(newItemAmount);

        ClaimItem updated = claimItemRepository.save(item);

        // Recalculate claim total
        recalculateClaimTotal(claim);

        return mapToResponse(updated);
    }

    private void recalculateClaimTotal(Claim claim) {
        List<ClaimItem> items = claimItemRepository.findByClaimId(claim.getId());
        long total = items.stream().mapToLong(i -> i.getAmount() != null ? i.getAmount() : 0L).sum();
        claim.setTotalClaim(total);
        claimRepository.save(claim);
    }

    private long calculateComponentUsage(String componentId, String excludeItemId) {
        List<ClaimItem> items = claimItemRepository.findByBudgetComponentId(componentId);
        return items.stream()
                .filter(i -> excludeItemId == null || !i.getId().equals(excludeItemId))
                .mapToLong(i -> i.getAmount() != null ? i.getAmount() : 0L)
                .sum();
    }

    private synchronized String generateNextClaimItemId() {
        List<ClaimItem> all = claimItemRepository.findAll();
        int maxSeq = 0;
        for (ClaimItem ci : all) {
            if (ci.getId() != null && ci.getId().startsWith("CLI")) {
                try {
                    int seq = Integer.parseInt(ci.getId().substring(3));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("CLI%04d", maxSeq + 1);
    }

    private ClaimItemResponse mapToResponse(ClaimItem item) {
        return ClaimItemResponse.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .claimId(item.getClaim() != null ? item.getClaim().getId() : null)
                .budgetComponentId(item.getBudgetComponent() != null ? item.getBudgetComponent().getId() : null)
                .componentName(item.getBudgetComponent() != null ? item.getBudgetComponent().getComponentName() : null)
                .rateBasis(item.getBudgetComponent() != null ? item.getBudgetComponent().getRateBasis() : null)
                .maxRate(item.getBudgetComponent() != null ? item.getBudgetComponent().getMaxRate() : null)
                .build();
    }
}
