package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimStatusRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimResponse;
import apap.ti._2026.simbg_2406349285_be.services.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping({"/claim", "/api/claim"})
    public ResponseEntity<BaseResponse<List<ClaimResponse>>> getAllClaims() {
        List<ClaimResponse> data = claimService.getAllClaims();
        return ResponseEntity.ok(BaseResponse.success(200, "Claims retrieved successfully", data));
    }

    @PostMapping({"/claim", "/api/claim"})
    public ResponseEntity<BaseResponse<ClaimResponse>> createClaim(@Valid @RequestBody CreateClaimRequest request) {
        ClaimResponse data = claimService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "Claim created successfully", data));
    }

    @GetMapping({"/claim/{id}", "/api/claim/{id}"})
    public ResponseEntity<BaseResponse<ClaimResponse>> getClaimById(@PathVariable("id") String id) {
        ClaimResponse data = claimService.getClaimById(id);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim retrieved successfully", data));
    }

    @PutMapping({"/claim/{id}", "/api/claim/{id}"})
    public ResponseEntity<BaseResponse<ClaimResponse>> updateClaim(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateClaimRequest request) {
        ClaimResponse data = claimService.updateClaim(id, request);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim updated successfully", data));
    }

    @PatchMapping({"/claim/{id}/status", "/api/claim/{id}/status"})
    public ResponseEntity<BaseResponse<ClaimResponse>> updateClaimStatus(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateClaimStatusRequest request) {
        ClaimResponse data = claimService.updateClaimStatus(id, request);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim status updated successfully", data));
    }
}
