package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetComponentResponse {
    private String id;
    private String componentName;
    private Long allocatedAmount;
    private Long maxRate;
    private String rateBasis;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String anggaranId;
    private Long usedAmount;
    private Long remainingAmount;
}

