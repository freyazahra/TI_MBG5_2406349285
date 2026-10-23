package apap.ti._2026.simbg_2406349285_be.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetComponentRequest {

    @NotBlank(message = "Nama komponen tidak boleh kosong")
    private String componentName;

    @NotNull(message = "Alokasi dana tidak boleh kosong")
    @Positive(message = "Alokasi dana harus lebih dari 0")
    private Long allocatedAmount;

    @NotNull(message = "Tarif maksimum tidak boleh kosong")
    @Positive(message = "Tarif maksimum harus lebih dari 0")
    private Long maxRate;

    @NotBlank(message = "Basis tarif tidak boleh kosong")
    @Pattern(regexp = "PER_PORSI|PER_HARI|TOTAL", message = "Basis tarif harus PER_PORSI, PER_HARI, atau TOTAL")
    private String rateBasis;

    private String description;
}
