package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateInvoiceRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.PaymentInvoiceResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.StatistikResponse;
import apap.ti._2026.simbg_2406349285_be.services.PaymentInvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentInvoiceController {

    private final PaymentInvoiceService paymentInvoiceService;

    @GetMapping({"/invoice", "/api/invoice"})
    public ResponseEntity<BaseResponse<List<PaymentInvoiceResponse>>> getAllInvoices() {
        List<PaymentInvoiceResponse> data = paymentInvoiceService.getAllInvoices();
        return ResponseEntity.ok(BaseResponse.success(200, "Invoices retrieved successfully", data));
    }

    @PostMapping({"/invoice", "/api/invoice"})
    public ResponseEntity<BaseResponse<PaymentInvoiceResponse>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        PaymentInvoiceResponse data = paymentInvoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "Invoice created successfully", data));
    }

    @GetMapping({"/invoice/statistik", "/api/invoice/statistik"})
    public ResponseEntity<BaseResponse<StatistikResponse>> getStatistik() {
        StatistikResponse data = paymentInvoiceService.getStatistik();
        return ResponseEntity.ok(BaseResponse.success(200, "Invoice statistics retrieved successfully", data));
    }

    @GetMapping({"/invoice/{invoiceNumber}", "/api/invoice/{invoiceNumber}"})
    public ResponseEntity<BaseResponse<PaymentInvoiceResponse>> getInvoiceByNumber(
            @PathVariable("invoiceNumber") String invoiceNumber) {
        PaymentInvoiceResponse data = paymentInvoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(BaseResponse.success(200, "Invoice retrieved successfully", data));
    }
}
