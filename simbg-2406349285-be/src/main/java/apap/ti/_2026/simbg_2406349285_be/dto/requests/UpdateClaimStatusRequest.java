package apap.ti._2026.simbg_2406349285_be.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimStatusRequest {

    @NotBlank(message = "Status baru tidak boleh kosong")
    private String status;

    private String approvedBy;

    private String rejectedReason;
}
