package apap.ti._2026.simbg_2406349285_be;

import apap.ti._2026.simbg_2406349285_be.dto.requests.*;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.services.ClaimItemService;
import apap.ti._2026.simbg_2406349285_be.services.ClaimService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClaimServiceTest {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private ClaimItemService claimItemService;

    @Test
    @DisplayName("Test Create Claim Item Exceeding Max Rate Should Throw")
    void testCreateClaimItemExceedingMaxRate() {
        CreateClaimItemRequest req = CreateClaimItemRequest.builder()
                .budgetComponentId("BGC0001") // maxRate is 100_000
                .description("Item Mahal")
                .quantity(1)
                .unitPrice(150_000L) // > 100_000
                .build();

        assertThrows(BadRequestException.class, () -> claimItemService.createItem("CLM0001", req));
    }

    @Test
    @DisplayName("Test Claim Status Transitions")
    void testClaimStatusTransitions() {
        // Add item to CLM0001 so it can be submitted
        CreateClaimItemRequest itemReq = CreateClaimItemRequest.builder()
                .budgetComponentId("BGC0001")
                .description("Nasi Box")
                .quantity(5)
                .unitPrice(20_000L)
                .build();
        claimItemService.createItem("CLM0001", itemReq);

        // DRAFT -> DIAJUKAN
        ClaimResponse submitted = claimService.updateClaimStatus("CLM0001",
                UpdateClaimStatusRequest.builder().status("DIAJUKAN").build());
        assertEquals("DIAJUKAN", submitted.getStatus());
        assertNotNull(submitted.getSubmittedAt());

        // DIAJUKAN -> DIVERIFIKASI
        ClaimResponse verified = claimService.updateClaimStatus("CLM0001",
                UpdateClaimStatusRequest.builder().status("DIVERIFIKASI").build());
        assertEquals("DIVERIFIKASI", verified.getStatus());

        // DIVERIFIKASI -> DISETUJUI
        ClaimResponse approved = claimService.updateClaimStatus("CLM0001",
                UpdateClaimStatusRequest.builder().status("DISETUJUI").approvedBy("Kepala Dinas").build());
        assertEquals("DISETUJUI", approved.getStatus());
        assertEquals("Kepala Dinas", approved.getApprovedBy());
        assertNotNull(approved.getApprovedAt());
    }

    @Test
    @DisplayName("Test Submit Claim Without Items Should Throw")
    void testSubmitClaimWithoutItems() {
        // CLM0002 has 0 items
        assertThrows(BadRequestException.class, () -> claimService.updateClaimStatus("CLM0002",
                UpdateClaimStatusRequest.builder().status("DIAJUKAN").build()));
    }

    @Test
    @DisplayName("Test Claim Can Only Be Rejected After Verification")
    void testRejectClaimAfterVerification() {
        UpdateClaimStatusRequest rejectRequest = UpdateClaimStatusRequest.builder()
                .status("DITOLAK")
                .rejectedReason("Dokumen tidak lengkap")
                .build();

        assertThrows(BadRequestException.class,
                () -> claimService.updateClaimStatus("CLM0002", rejectRequest));

        CreateClaimItemRequest itemReq = CreateClaimItemRequest.builder()
                .budgetComponentId("BGC0001")
                .description("Nasi Box")
                .quantity(1)
                .unitPrice(20_000L)
                .build();
        claimItemService.createItem("CLM0002", itemReq);

        claimService.updateClaimStatus("CLM0002",
                UpdateClaimStatusRequest.builder().status("DIAJUKAN").build());
        claimService.updateClaimStatus("CLM0002",
                UpdateClaimStatusRequest.builder().status("DIVERIFIKASI").build());

        ClaimResponse rejected = claimService.updateClaimStatus("CLM0002", rejectRequest);

        assertEquals("DITOLAK", rejected.getStatus());
        assertEquals("Dokumen tidak lengkap", rejected.getRejectedReason());
        assertEquals(1, rejected.getRejectionCount());
    }
}
