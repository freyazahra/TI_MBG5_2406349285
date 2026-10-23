package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.responses.DashboardResponse;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import apap.ti._2026.simbg_2406349285_be.models.Claim;
import apap.ti._2026.simbg_2406349285_be.models.PaymentInvoice;
import apap.ti._2026.simbg_2406349285_be.repositories.AnggaranRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.PaymentInvoiceRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.SekolahRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SekolahRepository sekolahRepository;
    private final AnggaranRepository anggaranRepository;
    private final PaymentInvoiceRepository paymentInvoiceRepository;
    private final ClaimRepository claimRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary() {
        long jumlahSekolah = sekolahRepository.count();

        List<Anggaran> allAnggaran = anggaranRepository.findAll();
        long totalAnggaran = allAnggaran.stream()
                .mapToLong(a -> a.getTotalAnggaran() != null ? a.getTotalAnggaran() : 0L)
                .sum();

        List<Claim> allClaims = claimRepository.findAll();
        long totalNominalClaim = allClaims.stream()
                .mapToLong(c -> c.getTotalClaim() != null ? c.getTotalClaim() : 0L)
                .sum();

        List<PaymentInvoice> allInvoices = paymentInvoiceRepository.findAll();
        long jumlahInvoice = allInvoices.size();
        long totalNominalInvoice = allInvoices.stream()
                .mapToLong(inv -> inv.getTotalAmount() != null ? inv.getTotalAmount() : 0L)
                .sum();

        return DashboardResponse.builder()
                .jumlahSekolah(jumlahSekolah)
                .totalAnggaran(totalAnggaran)
                .jumlahAnggaran(allAnggaran.size())
                .jumlahClaim(allClaims.size())
                .totalNominalClaim(totalNominalClaim)
                .jumlahInvoice(jumlahInvoice)
                .totalNominalInvoice(totalNominalInvoice)
                .build();
    }
}
