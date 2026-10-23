package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.BudgetComponentResponse;
import apap.ti._2026.simbg_2406349285_be.services.BudgetComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BudgetComponentController {

    private final BudgetComponentService budgetComponentService;

    @GetMapping({"/anggaran/{anggaranId}/komponen", "/api/anggaran/{anggaranId}/komponen"})
    public ResponseEntity<BaseResponse<List<BudgetComponentResponse>>> getComponents(
            @PathVariable("anggaranId") String anggaranId) {
        List<BudgetComponentResponse> data = budgetComponentService.getComponentsByAnggaranId(anggaranId);
        return ResponseEntity.ok(BaseResponse.success(200, "Budget components retrieved successfully", data));
    }

    @GetMapping({"/anggaran/{anggaranId}/komponen/{id}", "/api/anggaran/{anggaranId}/komponen/{id}"})
    public ResponseEntity<BaseResponse<BudgetComponentResponse>> getComponentById(
            @PathVariable("anggaranId") String anggaranId,
            @PathVariable("id") String id) {
        BudgetComponentResponse data = budgetComponentService.getComponentById(anggaranId, id);
        return ResponseEntity.ok(BaseResponse.success(200, "Budget component retrieved successfully", data));
    }

    @PostMapping({"/anggaran/{anggaranId}/komponen", "/api/anggaran/{anggaranId}/komponen"})
    public ResponseEntity<BaseResponse<BudgetComponentResponse>> createComponent(
            @PathVariable("anggaranId") String anggaranId,
            @Valid @RequestBody CreateBudgetComponentRequest request) {
        BudgetComponentResponse data = budgetComponentService.createComponent(anggaranId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "Budget component created successfully", data));
    }

    @PutMapping({"/anggaran/{anggaranId}/komponen/{id}", "/api/anggaran/{anggaranId}/komponen/{id}"})
    public ResponseEntity<BaseResponse<BudgetComponentResponse>> updateComponent(
            @PathVariable("anggaranId") String anggaranId,
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateBudgetComponentRequest request) {
        BudgetComponentResponse data = budgetComponentService.updateComponent(anggaranId, id, request);
        return ResponseEntity.ok(BaseResponse.success(200, "Budget component updated successfully", data));
    }
}
