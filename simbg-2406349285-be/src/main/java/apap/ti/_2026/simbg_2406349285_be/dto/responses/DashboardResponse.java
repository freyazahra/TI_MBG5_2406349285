package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long jumlahSekolah;
    private long totalAnggaran;
    private long jumlahAnggaran;
    private long jumlahClaim;
    private long totalNominalClaim;
    private long jumlahInvoice;
    private long totalNominalInvoice;
}

