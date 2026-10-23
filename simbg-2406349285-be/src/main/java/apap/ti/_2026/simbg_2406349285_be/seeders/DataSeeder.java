package apap.ti._2026.simbg_2406349285_be.seeders;

import apap.ti._2026.simbg_2406349285_be.models.*;
import apap.ti._2026.simbg_2406349285_be.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final SekolahRepository sekolahRepository;
    private final AnggaranRepository anggaranRepository;
    private final BudgetComponentRepository budgetComponentRepository;
    private final ClaimRepository claimRepository;
    private final ClaimItemRepository claimItemRepository;
    private final PaymentInvoiceRepository paymentInvoiceRepository;

    @Override
    public void run(String... args) {
        if (sekolahRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Seed 10 Sekolah
        List<Sekolah> schools = List.of(
                Sekolah.builder().id("SKL0001").nama("SMP Toli-toli").educationLevel("SMP").address("Apt. 934 Jl. Gajahmada No. 40, Baru, NJ 91755").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0002").nama("SMA Bangka Barat").educationLevel("SMA").address("Jl. Juanda No. 12, Bukittinggi, DE 18895").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0003").nama("SD Pasuruan").educationLevel("SD").address("Suite 221 Jl. Gajahmada No. 80, Bolaang Mongondow Timur, CA 66394").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0004").nama("SMP Sumba Timur").educationLevel("SMP").address("Apt. 507 Jl. Kartini No. 61, Kebumen, OR 83310").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0005").nama("SD Pasuruan").educationLevel("SD").address("Jl. Hayamwuruk No. 85, Gayo Lues, WY 07293").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0006").nama("SMP Way Kanan").educationLevel("SMP").address("Jl. Ahmad Yani No. 85, Singkawang, PA 31915").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0007").nama("SD Padang").educationLevel("SD").address("Suite 364 Jl. Ahmad Yani No. 82, Ogan Ilir, SC 67051").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0008").nama("SMA Jakarta Barat").educationLevel("SMA").address("Jl. Kartini No. 34, Balikpapan, RI 42990").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0009").nama("SMA Bolaang Mongondow Utara").educationLevel("SMA").address("Jl. Gajahmada No. 28, Hulu Sungai Selatan, IN 88440").createdAt(now).updatedAt(now).build(),
                Sekolah.builder().id("SKL0010").nama("SMA Paser").educationLevel("SMA").address("Jl. Gatot Soebroto No. 72, Sawah Lunto, MD 01920").createdAt(now).updatedAt(now).build()
        );
        sekolahRepository.saveAll(schools);

        Sekolah skl1 = schools.get(0);

        // 2. Seed Anggaran
        Anggaran ang1 = Anggaran.builder()
                .id("ANG0001")
                .sekolah(skl1)
                .startPeriod(LocalDate.of(2024, 1, 1))
                .endPeriod(LocalDate.of(2024, 12, 31))
                .totalAnggaran(150_000_000L)
                .status("AKTIF")
                .description("Anggaran operasional")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Anggaran ang2 = Anggaran.builder()
                .id("ANG0002")
                .sekolah(skl1)
                .startPeriod(LocalDate.of(2024, 6, 1))
                .endPeriod(LocalDate.of(2024, 12, 31))
                .totalAnggaran(150_000_000L)
                .status("AKTIF")
                .description("Anggaran semester 2")
                .createdAt(now)
                .updatedAt(now)
                .build();

        anggaranRepository.saveAll(List.of(ang1, ang2));

        // 3. Seed Budget Components for ANG0001
        BudgetComponent bgc1 = BudgetComponent.builder()
                .id("BGC0001")
                .anggaran(ang1)
                .componentName("Nasi")
                .allocatedAmount(5_000_000L)
                .maxRate(100_000L)
                .rateBasis("PER_PORSI")
                .description("Deskripsi komponen")
                .createdAt(now)
                .updatedAt(now)
                .build();

        BudgetComponent bgc2 = BudgetComponent.builder()
                .id("BGC0002")
                .anggaran(ang1)
                .componentName("Minyak")
                .allocatedAmount(5_000_000L)
                .maxRate(100_000L)
                .rateBasis("PER_PORSI")
                .description("Deskripsi komponen")
                .createdAt(now)
                .updatedAt(now)
                .build();

        budgetComponentRepository.saveAll(List.of(bgc1, bgc2));

        // 4. Seed Claims
        Claim clm1 = Claim.builder()
                .id("CLM0001")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(0L)
                .status("DRAFT")
                .description("Deskripsi claim")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm2 = Claim.builder()
                .id("CLM0002")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 18))
                .totalClaim(0L)
                .status("DRAFT")
                .description("Revisi deskripsi")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm3 = Claim.builder()
                .id("CLM0003")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(0L)
                .status("DISETUJUI")
                .approvedBy("John Doe")
                .approvedAt(now)
                .description("Deskripsi claim")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm4 = Claim.builder()
                .id("CLM0004")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(500_000L)
                .status("DISETUJUI")
                .approvedBy("John Doe")
                .approvedAt(now)
                .description("Klaim bahan makanan")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm5 = Claim.builder()
                .id("CLM0005")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(300_000L)
                .status("DISETUJUI")
                .approvedBy("John Doe")
                .approvedAt(now)
                .description("Klaim operasional")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm6 = Claim.builder()
                .id("CLM0006")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(0L)
                .status("DIAJUKAN")
                .submittedAt(now)
                .description("Klaim diajukan")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm7 = Claim.builder()
                .id("CLM0007")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2026, 9, 30))
                .totalClaim(36_000L)
                .status("DISETUJUI")
                .approvedBy("John Doe")
                .approvedAt(now)
                .description("Pembuatan makanan untuk bulan oktober")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Claim clm8 = Claim.builder()
                .id("CLM0008")
                .anggaran(ang1)
                .claimDate(LocalDate.of(2024, 5, 15))
                .totalClaim(21_000L)
                .status("DITOLAK")
                .rejectedReason("masih belum jelas")
                .rejectionCount(1)
                .description("Klaim ditolak")
                .createdAt(now)
                .updatedAt(now)
                .build();

        claimRepository.saveAll(List.of(clm1, clm2, clm3, clm4, clm5, clm6, clm7, clm8));

        // 5. Seed Claim Items
        ClaimItem cli1 = ClaimItem.builder()
                .id("CLI0001")
                .claim(clm4)
                .budgetComponent(bgc1)
                .description("Nasi")
                .quantity(2)
                .unitPrice(100_000L)
                .amount(200_000L)
                .build();

        ClaimItem cli2 = ClaimItem.builder()
                .id("CLI0002")
                .claim(clm4)
                .budgetComponent(bgc2)
                .description("Nasi")
                .quantity(3)
                .unitPrice(100_000L)
                .amount(300_000L)
                .build();

        ClaimItem cli3 = ClaimItem.builder()
                .id("CLI0003")
                .claim(clm5)
                .budgetComponent(bgc2)
                .description("Minyak")
                .quantity(3)
                .unitPrice(100_000L)
                .amount(300_000L)
                .build();

        ClaimItem cli4 = ClaimItem.builder()
                .id("CLI0004")
                .claim(clm7)
                .budgetComponent(bgc1)
                .description("Gula")
                .quantity(1)
                .unitPrice(36_000L)
                .amount(36_000L)
                .build();

        ClaimItem cli5 = ClaimItem.builder()
                .id("CLI0005")
                .claim(clm8)
                .budgetComponent(bgc1)
                .description("Bumbu")
                .quantity(1)
                .unitPrice(21_000L)
                .amount(21_000L)
                .build();

        claimItemRepository.saveAll(List.of(cli1, cli2, cli3, cli4, cli5));

        // 6. Seed Invoices
        PaymentInvoice inv1 = PaymentInvoice.builder()
                .invoiceNumber("INV-MBG-2024-0001")
                .claim(clm4)
                .invoiceDate(LocalDate.of(2024, 6, 1))
                .dueDate(LocalDate.of(2024, 6, 15))
                .totalAmount(500_000L)
                .status("BELUM_DIBAYAR")
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentInvoice inv2 = PaymentInvoice.builder()
                .invoiceNumber("INV-MBG-2024-0002")
                .claim(clm5)
                .invoiceDate(LocalDate.of(2024, 6, 1))
                .dueDate(LocalDate.of(2024, 6, 15))
                .totalAmount(300_000L)
                .status("BELUM_DIBAYAR")
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentInvoice inv3 = PaymentInvoice.builder()
                .invoiceNumber("INV-MBG-2026-0001")
                .claim(clm7)
                .invoiceDate(LocalDate.of(2026, 9, 1))
                .dueDate(LocalDate.of(2026, 9, 15))
                .totalAmount(36_000L)
                .status("BELUM_DIBAYAR")
                .createdAt(now)
                .updatedAt(now)
                .build();

        paymentInvoiceRepository.saveAll(List.of(inv1, inv2, inv3));
    }
}
