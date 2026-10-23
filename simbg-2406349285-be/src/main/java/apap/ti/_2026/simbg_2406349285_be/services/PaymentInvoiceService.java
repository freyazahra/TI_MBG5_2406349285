package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateInvoiceRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.PaymentInvoiceResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.StatistikResponse;
import apap.ti._2026.simbg_2406349285_be.models.PaymentInvoice;

import java.util.List;

public interface PaymentInvoiceService {
    List<PaymentInvoiceResponse> getAllInvoices();
    PaymentInvoiceResponse getInvoiceByNumber(String invoiceNumber);
    PaymentInvoice getInvoiceEntityByNumber(String invoiceNumber);
    PaymentInvoiceResponse createInvoice(CreateInvoiceRequest request);
    StatistikResponse getStatistik();
}
