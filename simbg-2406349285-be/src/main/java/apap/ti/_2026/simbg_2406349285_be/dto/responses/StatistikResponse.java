package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistikResponse {

    @Builder.Default
    private List<ClaimStatItem> claimStatistics = new ArrayList<>();

    @Builder.Default
    private List<InvoiceStatItem> invoiceStatistics = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimStatItem {
        private String status;
        private long jumlah;
        private long totalNominal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceStatItem {
        private String status;
        private long jumlah;
        private long totalNominal;
    }
}
