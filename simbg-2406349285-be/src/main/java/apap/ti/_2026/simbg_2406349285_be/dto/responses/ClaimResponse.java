package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {
    private String id;
    private LocalDate claimDate;
    private Long totalClaim;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String approvedBy;
    private String rejectedReason;
    private Integer rejectionCount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String anggaranId;
    private String namaSekolah;
    private Integer itemCount;
    @Builder.Default
    private List<ClaimItemResponse> claimItems = new ArrayList<>();
}

