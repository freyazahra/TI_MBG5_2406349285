package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnggaranResponse {
    private String id;
    private LocalDate startPeriod;
    private LocalDate endPeriod;
    private Long totalAnggaran;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String sekolahId;
    private String namaSekolah;
    private Long totalAllocated;
    private Long remainingAmount;
}

