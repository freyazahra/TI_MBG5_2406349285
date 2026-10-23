package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimItemResponse {
    private String id;
    private String description;
    private Integer quantity;
    private Long unitPrice;
    private Long amount;
    private String claimId;
    private String budgetComponentId;
    private String componentName;
    private String rateBasis;
    private Long maxRate;
}

