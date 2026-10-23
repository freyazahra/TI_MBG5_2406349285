package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateAnggaranRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.AnggaranResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import apap.ti._2026.simbg_2406349285_be.models.BudgetComponent;
import apap.ti._2026.simbg_2406349285_be.models.Sekolah;
import apap.ti._2026.simbg_2406349285_be.repositories.AnggaranRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.BudgetComponentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnggaranServiceImpl implements AnggaranService {

    private final AnggaranRepository anggaranRepository;
    private final BudgetComponentRepository budgetComponentRepository;
    private final SekolahService sekolahService;

    @Override
    @Transactional(readOnly = true)
    public List<AnggaranResponse> getAllAnggaran() {
        return anggaranRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AnggaranResponse getAnggaranById(String id) {
        return mapToResponse(getAnggaranEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Anggaran getAnggaranEntityById(String id) {
        return anggaranRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anggaran tidak ditemukan: " + id));
    }

    @Override
    @Transactional
    public AnggaranResponse createAnggaran(CreateAnggaranRequest request) {
        if (request.getEndPeriod().isBefore(request.getStartPeriod())) {
            throw new BadRequestException("Tanggal akhir periode tidak boleh lebih awal dari tanggal mulai periode");
        }
        if (request.getTotalAnggaran() == null || request.getTotalAnggaran() <= 0) {
            throw new BadRequestException("Total anggaran harus lebih dari 0");
        }

        Sekolah sekolah = sekolahService.getSchoolById(request.getSekolahId());

        String generatedId = generateNextAnggaranId();
        LocalDateTime now = LocalDateTime.now();

        String status = (request.getStatus() != null && !request.getStatus().isBlank())
                ? request.getStatus().toUpperCase()
                : "AKTIF";
        if (!"AKTIF".equals(status) && !"NONAKTIF".equals(status)) {
            throw new BadRequestException("Status anggaran harus AKTIF atau NONAKTIF");
        }

        Anggaran anggaran = Anggaran.builder()
                .id(generatedId)
                .sekolah(sekolah)
                .startPeriod(request.getStartPeriod())
                .endPeriod(request.getEndPeriod())
                .totalAnggaran(request.getTotalAnggaran())
                .status(status)
                .description(request.getDescription())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Anggaran saved = anggaranRepository.save(anggaran);
        return mapToResponse(saved);
    }

    private synchronized String generateNextAnggaranId() {
        List<Anggaran> all = anggaranRepository.findAll();
        int maxSeq = 0;
        for (Anggaran a : all) {
            if (a.getId() != null && a.getId().startsWith("ANG")) {
                try {
                    int seq = Integer.parseInt(a.getId().substring(3));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("ANG%04d", maxSeq + 1);
    }

    private AnggaranResponse mapToResponse(Anggaran a) {
        List<BudgetComponent> components = budgetComponentRepository.findByAnggaranId(a.getId());
        long totalAllocated = components.stream()
                .mapToLong(c -> c.getAllocatedAmount() != null ? c.getAllocatedAmount() : 0L)
                .sum();
        long remaining = a.getTotalAnggaran() - totalAllocated;

        return AnggaranResponse.builder()
                .id(a.getId())
                .startPeriod(a.getStartPeriod())
                .endPeriod(a.getEndPeriod())
                .totalAnggaran(a.getTotalAnggaran())
                .status(a.getStatus())
                .description(a.getDescription())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .sekolahId(a.getSekolah() != null ? a.getSekolah().getId() : null)
                .namaSekolah(a.getSekolah() != null ? a.getSekolah().getNama() : null)
                .totalAllocated(totalAllocated)
                .remainingAmount(remaining)
                .build();
    }
}
