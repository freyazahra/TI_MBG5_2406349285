package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.DashboardResponse;
import apap.ti._2026.simbg_2406349285_be.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final DashboardService dashboardService;

    @GetMapping({"/home", "/api/home"})
    public ResponseEntity<BaseResponse<DashboardResponse>> getDashboard() {
        DashboardResponse data = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(BaseResponse.success(200, "Dashboard retrieved successfully", data));
    }
}
