package apap.ti._2026.simbg_2406349285_be.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimItemRequest {

    @NotBlank(message = "Budget Component ID tidak boleh kosong")
    private String budgetComponentId;

    @NotBlank(message = "Deskripsi item tidak boleh kosong")
    private String description;

    @NotNull(message = "Kuantitas tidak boleh kosong")
    @Positive(message = "Kuantitas harus lebih dari 0")
    private Integer quantity;

    @NotNull(message = "Harga satuan tidak boleh kosong")
    @Positive(message = "Harga satuan harus lebih dari 0")
    private Long unitPrice;
}
