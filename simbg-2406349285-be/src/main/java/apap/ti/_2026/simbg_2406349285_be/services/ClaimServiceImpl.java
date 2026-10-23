package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimStatusRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import apap.ti._2026.simbg_2406349285_be.models.Claim;
import apap.ti._2026.simbg_2406349285_be.models.ClaimItem;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimItemRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimItemRepository claimItemRepository;
    private final AnggaranService anggaranService;

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(String id) {
        return mapToResponse(getClaimEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Claim getClaimEntityById(String id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim tidak ditemukan: " + id));
    }

    @Override
    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {
        Anggaran anggaran = anggaranService.getAnggaranEntityById(request.getAnggaranId());

        String generatedId = generateNextClaimId();
        LocalDateTime now = LocalDateTime.now();

        Claim claim = Claim.builder()
                .id(generatedId)
                .anggaran(anggaran)
                .claimDate(request.getClaimDate())
                .totalClaim(0L)
                .status("DRAFT")
                .description(request.getDescription())
                .rejectionCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim saved = claimRepository.save(claim);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ClaimResponse updateClaim(String id, UpdateClaimRequest request) {
        Claim claim = getClaimEntityById(id);

        if (!"DRAFT".equalsIgnoreCase(claim.getStatus())) {
            throw new BadRequestException("Perubahan hanya dapat dilakukan pada claim berstatus DRAFT");
        }

        claim.setClaimDate(request.getClaimDate());
        claim.setDescription(request.getDescription());
        claim.setUpdatedAt(LocalDateTime.now());

        Claim updated = claimRepository.save(claim);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public ClaimResponse updateClaimStatus(String id, UpdateClaimStatusRequest request) {
        Claim claim = getClaimEntityById(id);
        String currentStatus = claim.getStatus().toUpperCase();
        String targetStatus = request.getStatus().toUpperCase();

        if (currentStatus.equals(targetStatus)) {
            return mapToResponse(claim);
        }

        LocalDateTime now = LocalDateTime.now();

        // Validate transitions:
        // DRAFT -> DIAJUKAN, DIBATALKAN
        // DIAJUKAN -> DIVERIFIKASI, DIBATALKAN
        // DIVERIFIKASI -> DISETUJUI, DITOLAK
        // DITOLAK -> DIAJUKAN (or DRAFT) max 3 times
        switch (targetStatus) {
            case "DIAJUKAN":
                if ("DRAFT".equals(currentStatus)) {
                    List<ClaimItem> items = claimItemRepository.findByClaimId(id);
                    if (items.isEmpty()) {
                        throw new BadRequestException("Claim harus memiliki minimal satu ClaimItem untuk diajukan");
                    }
                    claim.setStatus("DIAJUKAN");
                    claim.setSubmittedAt(now);
                } else if ("DITOLAK".equals(currentStatus)) {
                    if (claim.getRejectionCount() >= 3) {
                        throw new BadRequestException("Claim telah ditolak 3 kali dan tidak dapat diajukan kembali");
                    }
                    claim.setStatus("DIAJUKAN");
                    claim.setSubmittedAt(now);
                } else {
                    throw new BadRequestException("Transisi status dari " + currentStatus + " ke DIAJUKAN tidak diperbolehkan");
                }
                break;

            case "DIVERIFIKASI":
                if (!"DIAJUKAN".equals(currentStatus)) {
                    throw new BadRequestException("Transisi status dari " + currentStatus + " ke DIVERIFIKASI tidak diperbolehkan");
                }
                claim.setStatus("DIVERIFIKASI");
                break;

            case "DISETUJUI":
                if (!"DIVERIFIKASI".equals(currentStatus)) {
                    throw new BadRequestException("Transisi status dari " + currentStatus + " ke DISETUJUI tidak diperbolehkan");
                }
                if (request.getApprovedBy() == null || request.getApprovedBy().isBlank()) {
                    throw new BadRequestException("Nama pemberi persetujuan (approvedBy) wajib diisi");
                }
                claim.setStatus("DISETUJUI");
                claim.setApprovedAt(now);
                claim.setApprovedBy(request.getApprovedBy());
                break;

            case "DITOLAK":
                if (!"DIVERIFIKASI".equals(currentStatus)) {
                    throw new BadRequestException("Transisi status dari " + currentStatus + " ke DITOLAK tidak diperbolehkan");
                }
                if (request.getRejectedReason() == null || request.getRejectedReason().isBlank()) {
                    throw new BadRequestException("Alasan penolakan (rejectedReason) wajib diisi");
                }
                claim.setStatus("DITOLAK");
                claim.setRejectedReason(request.getRejectedReason());
                claim.setRejectionCount(claim.getRejectionCount() + 1);
                break;

            case "DIBATALKAN":
                if (!"DRAFT".equals(currentStatus) && !"DIAJUKAN".equals(currentStatus)) {
                    throw new BadRequestException("Hanya claim dengan status DRAFT atau DIAJUKAN yang dapat dibatalkan");
                }
                claim.setStatus("DIBATALKAN");
                break;

            default:
                throw new BadRequestException("Status target tidak valid: " + targetStatus);
        }

        claim.setUpdatedAt(now);
        Claim updated = claimRepository.save(claim);
        return mapToResponse(updated);
    }

    private synchronized String generateNextClaimId() {
        List<Claim> all = claimRepository.findAll();
        int maxSeq = 0;
        for (Claim c : all) {
            if (c.getId() != null && c.getId().startsWith("CLM")) {
                try {
                    int seq = Integer.parseInt(c.getId().substring(3));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("CLM%04d", maxSeq + 1);
    }

    private ClaimResponse mapToResponse(Claim c) {
        List<ClaimItem> items = claimItemRepository.findByClaimId(c.getId());
        List<ClaimItemResponse> itemResponses = items.stream().map(item -> ClaimItemResponse.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .claimId(c.getId())
                .budgetComponentId(item.getBudgetComponent() != null ? item.getBudgetComponent().getId() : null)
                .componentName(item.getBudgetComponent() != null ? item.getBudgetComponent().getComponentName() : null)
                .rateBasis(item.getBudgetComponent() != null ? item.getBudgetComponent().getRateBasis() : null)
                .maxRate(item.getBudgetComponent() != null ? item.getBudgetComponent().getMaxRate() : null)
                .build()).collect(Collectors.toList());

        String namaSekolah = null;
        if (c.getAnggaran() != null && c.getAnggaran().getSekolah() != null) {
            namaSekolah = c.getAnggaran().getSekolah().getNama();
        }

        return ClaimResponse.builder()
                .id(c.getId())
                .claimDate(c.getClaimDate())
                .totalClaim(c.getTotalClaim())
                .status(c.getStatus())
                .submittedAt(c.getSubmittedAt())
                .approvedAt(c.getApprovedAt())
                .approvedBy(c.getApprovedBy())
                .rejectedReason(c.getRejectedReason())
                .rejectionCount(c.getRejectionCount())
                .description(c.getDescription())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .anggaranId(c.getAnggaran() != null ? c.getAnggaran().getId() : null)
                .namaSekolah(namaSekolah)
                .itemCount(items.size())
                .claimItems(itemResponses)
                .build();
    }
}
