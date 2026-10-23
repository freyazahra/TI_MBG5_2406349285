package apap.ti._2026.simbg_2406349285_be.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnggaranRequest {

    @NotNull(message = "Sekolah ID tidak boleh kosong")
    private String sekolahId;

    @NotNull(message = "Tanggal mulai periode tidak boleh kosong")
    private LocalDate startPeriod;

    @NotNull(message = "Tanggal akhir periode tidak boleh kosong")
    private LocalDate endPeriod;

    @NotNull(message = "Total anggaran tidak boleh kosong")
    @Positive(message = "Total anggaran harus lebih dari 0")
    private Long totalAnggaran;

    private String status;

    private String description;
}
