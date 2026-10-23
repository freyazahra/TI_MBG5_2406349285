package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;
import apap.ti._2026.simbg_2406349285_be.services.ClaimItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClaimItemController {

    private final ClaimItemService claimItemService;

    @GetMapping({"/claim/{claimId}/item", "/api/claim/{claimId}/item"})
    public ResponseEntity<BaseResponse<List<ClaimItemResponse>>> getItems(@PathVariable("claimId") String claimId) {
        List<ClaimItemResponse> data = claimItemService.getItemsByClaimId(claimId);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim items retrieved successfully", data));
    }

    @GetMapping({"/claim/{claimId}/item/{id}", "/api/claim/{claimId}/item/{id}"})
    public ResponseEntity<BaseResponse<ClaimItemResponse>> getItemById(
            @PathVariable("claimId") String claimId,
            @PathVariable("id") String id) {
        ClaimItemResponse data = claimItemService.getItemById(claimId, id);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim item retrieved successfully", data));
    }

    @PostMapping({"/claim/{claimId}/item", "/api/claim/{claimId}/item"})
    public ResponseEntity<BaseResponse<ClaimItemResponse>> createItem(
            @PathVariable("claimId") String claimId,
            @Valid @RequestBody CreateClaimItemRequest request) {
        ClaimItemResponse data = claimItemService.createItem(claimId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "Claim item created successfully", data));
    }

    @PutMapping({"/claim/{claimId}/item/{id}", "/api/claim/{claimId}/item/{id}"})
    public ResponseEntity<BaseResponse<ClaimItemResponse>> updateItem(
            @PathVariable("claimId") String claimId,
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateClaimItemRequest request) {
        ClaimItemResponse data = claimItemService.updateItem(claimId, id, request);
        return ResponseEntity.ok(BaseResponse.success(200, "Claim item updated successfully", data));
    }
}
