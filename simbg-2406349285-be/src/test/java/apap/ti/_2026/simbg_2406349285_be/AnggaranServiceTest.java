package apap.ti._2026.simbg_2406349285_be;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateAnggaranRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.AnggaranResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.BudgetComponentResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.BadRequestException;
import apap.ti._2026.simbg_2406349285_be.services.AnggaranService;
import apap.ti._2026.simbg_2406349285_be.services.BudgetComponentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AnggaranServiceTest {

    @Autowired
    private AnggaranService anggaranService;

    @Autowired
    private BudgetComponentService budgetComponentService;

    @Test
    @DisplayName("Test Create Anggaran Success")
    void testCreateAnggaranSuccess() {
        CreateAnggaranRequest request = CreateAnggaranRequest.builder()
                .sekolahId("SKL0001")
                .startPeriod(LocalDate.of(2025, 1, 1))
                .endPeriod(LocalDate.of(2025, 12, 31))
                .totalAnggaran(50_000_000L)
                .status("AKTIF")
                .description("Anggaran Baru")
                .build();

        AnggaranResponse response = anggaranService.createAnggaran(request);

        assertNotNull(response.getId());
        assertEquals("SKL0001", response.getSekolahId());
        assertEquals(50_000_000L, response.getTotalAnggaran());
        assertEquals(50_000_000L, response.getRemainingAmount());
    }

    @Test
    @DisplayName("Test Create Anggaran EndPeriod Before StartPeriod Should Throw")
    void testCreateAnggaranInvalidPeriod() {
        CreateAnggaranRequest request = CreateAnggaranRequest.builder()
                .sekolahId("SKL0001")
                .startPeriod(LocalDate.of(2025, 12, 31))
                .endPeriod(LocalDate.of(2025, 1, 1))
                .totalAnggaran(50_000_000L)
                .build();

        assertThrows(BadRequestException.class, () -> anggaranService.createAnggaran(request));
    }

    @Test
    @DisplayName("Test Create Anggaran With Invalid Status Should Throw")
    void testCreateAnggaranInvalidStatus() {
        CreateAnggaranRequest request = CreateAnggaranRequest.builder()
                .sekolahId("SKL0001")
                .startPeriod(LocalDate.of(2025, 1, 1))
                .endPeriod(LocalDate.of(2025, 12, 31))
                .totalAnggaran(50_000_000L)
                .status("ARSIP")
                .build();

        assertThrows(BadRequestException.class, () -> anggaranService.createAnggaran(request));
    }

    @Test
    @DisplayName("Test Create Budget Component Exceeding Total Anggaran Should Throw")
    void testCreateBudgetComponentExceedingAnggaran() {
        CreateAnggaranRequest angReq = CreateAnggaranRequest.builder()
                .sekolahId("SKL0001")
                .startPeriod(LocalDate.of(2025, 1, 1))
                .endPeriod(LocalDate.of(2025, 12, 31))
                .totalAnggaran(10_000_000L)
                .build();
        AnggaranResponse ang = anggaranService.createAnggaran(angReq);

        CreateBudgetComponentRequest bgcReq = CreateBudgetComponentRequest.builder()
                .componentName("Beras")
                .allocatedAmount(12_000_000L) // > 10_000_000
                .maxRate(10_000L)
                .rateBasis("PER_PORSI")
                .build();

        assertThrows(BadRequestException.class, () -> budgetComponentService.createComponent(ang.getId(), bgcReq));
    }
}
