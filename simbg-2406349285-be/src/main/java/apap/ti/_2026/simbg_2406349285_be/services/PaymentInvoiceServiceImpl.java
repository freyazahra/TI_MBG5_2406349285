package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateInvoiceRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.PaymentInvoiceResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.StatistikResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.Claim;
import apap.ti._2026.simbg_2406349285_be.models.ClaimItem;
import apap.ti._2026.simbg_2406349285_be.models.PaymentInvoice;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimItemRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.ClaimRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.PaymentInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentInvoiceServiceImpl implements PaymentInvoiceService {

    private final PaymentInvoiceRepository paymentInvoiceRepository;
    private final ClaimRepository claimRepository;
    private final ClaimItemRepository claimItemRepository;
    private final ClaimService claimService;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentInvoiceResponse> getAllInvoices() {
        return paymentInvoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        return mapToResponse(getInvoiceEntityByNumber(invoiceNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInvoice getInvoiceEntityByNumber(String invoiceNumber) {
        return paymentInvoiceRepository.findById(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice tidak ditemukan: " + invoiceNumber));
    }

    @Override
    @Transactional
    public PaymentInvoiceResponse createInvoice(CreateInvoiceRequest request) {
        Claim claim = claimService.getClaimEntityById(request.getClaimId());

        if (!"DISETUJUI".equalsIgnoreCase(claim.getStatus())) {
            throw new BadRequestException("Invoice hanya dapat diterbitkan untuk claim yang berstatus DISETUJUI");
        }

        if (paymentInvoiceRepository.findByClaimId(claim.getId()).isPresent()) {
            throw new BadRequestException("Claim tersebut sudah pernah diterbitkan invoice-nya");
        }

        if (!request.getDueDate().isAfter(request.getInvoiceDate())) {
            throw new BadRequestException("Tanggal jatuh tempo harus lebih besar dari tanggal invoice");
        }

        String generatedNumber = generateInvoiceNumber(request.getInvoiceDate().getYear());
        LocalDateTime now = LocalDateTime.now();

        PaymentInvoice invoice = PaymentInvoice.builder()
                .invoiceNumber(generatedNumber)
                .claim(claim)
                .invoiceDate(request.getInvoiceDate())
                .dueDate(request.getDueDate())
                .totalAmount(claim.getTotalClaim())
                .status("BELUM_DIBAYAR")
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentInvoice saved = paymentInvoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StatistikResponse getStatistik() {
        // Claim statistics for DIAJUKAN, DISETUJUI, DITOLAK, DIBATALKAN
        List<String> targetStatuses = List.of("DIAJUKAN", "DISETUJUI", "DITOLAK", "DIBATALKAN");
        List<Claim> allClaims = claimRepository.findAll();

        List<StatistikResponse.ClaimStatItem> claimStats = new ArrayList<>();
        for (String st : targetStatuses) {
            long count = allClaims.stream().filter(c -> st.equalsIgnoreCase(c.getStatus())).count();
            long sum = allClaims.stream().filter(c -> st.equalsIgnoreCase(c.getStatus()))
                    .mapToLong(c -> c.getTotalClaim() != null ? c.getTotalClaim() : 0L).sum();
            claimStats.add(new StatistikResponse.ClaimStatItem(st, count, sum));
        }

        // Invoice statistics grouped by month
        List<PaymentInvoice> allInvoices = paymentInvoiceRepository.findAll();
        Map<String, List<PaymentInvoice>> groupedByMonth = allInvoices.stream()
                .collect(Collectors.groupingBy(inv -> {
                    LocalDate d = inv.getInvoiceDate();
                    return String.format("%04d-%02d-01", d.getYear(), d.getMonthValue());
                }, TreeMap::new, Collectors.toList()));

        List<StatistikResponse.InvoiceStatItem> invoiceStats = new ArrayList<>();
        for (Map.Entry<String, List<PaymentInvoice>> entry : groupedByMonth.entrySet()) {
            long count = entry.getValue().size();
            long sum = entry.getValue().stream()
                    .mapToLong(inv -> inv.getTotalAmount() != null ? inv.getTotalAmount() : 0L).sum();
            invoiceStats.add(new StatistikResponse.InvoiceStatItem(entry.getKey(), count, sum));
        }

        return StatistikResponse.builder()
                .claimStatistics(claimStats)
                .invoiceStatistics(invoiceStats)
                .build();
    }

    private synchronized String generateInvoiceNumber(int year) {
        String prefix = String.format("INV-MBG-%d-", year);
        List<PaymentInvoice> all = paymentInvoiceRepository.findAll();
        int maxSeq = 0;
        for (PaymentInvoice inv : all) {
            if (inv.getInvoiceNumber() != null && inv.getInvoiceNumber().startsWith(prefix)) {
                try {
                    int seq = Integer.parseInt(inv.getInvoiceNumber().substring(prefix.length()));
                    if (seq > maxSeq) {
                        maxSeq = seq;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("%s%04d", prefix, maxSeq + 1);
    }

    private PaymentInvoiceResponse mapToResponse(PaymentInvoice inv) {
        Claim claim = inv.getClaim();
        List<ClaimItemResponse> itemResponses = new ArrayList<>();
        LocalDate claimDate = null;
        String claimId = null;

        if (claim != null) {
            claimId = claim.getId();
            claimDate = claim.getClaimDate();
            List<ClaimItem> items = claimItemRepository.findByClaimId(claim.getId());
            itemResponses = items.stream().map(item -> ClaimItemResponse.builder()
                    .id(item.getId())
                    .description(item.getDescription())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .amount(item.getAmount())
                    .claimId(claim.getId())
                    .budgetComponentId(item.getBudgetComponent() != null ? item.getBudgetComponent().getId() : null)
                    .componentName(item.getBudgetComponent() != null ? item.getBudgetComponent().getComponentName() : null)
                    .rateBasis(item.getBudgetComponent() != null ? item.getBudgetComponent().getRateBasis() : null)
                    .maxRate(item.getBudgetComponent() != null ? item.getBudgetComponent().getMaxRate() : null)
                    .build()).collect(Collectors.toList());
        }

        return PaymentInvoiceResponse.builder()
                .invoiceNumber(inv.getInvoiceNumber())
                .invoiceDate(inv.getInvoiceDate())
                .dueDate(inv.getDueDate())
                .totalAmount(inv.getTotalAmount())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .claimId(claimId)
                .claimDate(claimDate)
                .claimItems(itemResponses)
                .build();
    }
}
