package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimItemRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimItemResponse;

import java.util.List;

public interface ClaimItemService {
    List<ClaimItemResponse> getItemsByClaimId(String claimId);
    ClaimItemResponse getItemById(String claimId, String id);
    ClaimItemResponse createItem(String claimId, CreateClaimItemRequest request);
    ClaimItemResponse updateItem(String claimId, String id, UpdateClaimItemRequest request);
}
