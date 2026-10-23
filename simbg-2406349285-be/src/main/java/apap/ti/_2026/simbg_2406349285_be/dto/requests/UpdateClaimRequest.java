package apap.ti._2026.simbg_2406349285_be.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimRequest {

    @NotNull(message = "Tanggal claim tidak boleh kosong")
    private LocalDate claimDate;

    private String description;
}
