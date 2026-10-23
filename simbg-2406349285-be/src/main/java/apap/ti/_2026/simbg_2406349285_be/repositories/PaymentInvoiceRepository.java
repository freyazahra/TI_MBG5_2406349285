package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.PaymentInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentInvoiceRepository extends JpaRepository<PaymentInvoice, String> {
    Optional<PaymentInvoice> findByClaimId(String claimId);
}

