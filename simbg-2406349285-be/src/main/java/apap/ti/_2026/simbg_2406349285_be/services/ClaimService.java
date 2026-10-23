package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateClaimStatusRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.ClaimResponse;
import apap.ti._2026.simbg_2406349285_be.models.Claim;

import java.util.List;

public interface ClaimService {
    List<ClaimResponse> getAllClaims();
    ClaimResponse getClaimById(String id);
    Claim getClaimEntityById(String id);
    ClaimResponse createClaim(CreateClaimRequest request);
    ClaimResponse updateClaim(String id, UpdateClaimRequest request);
    ClaimResponse updateClaimStatus(String id, UpdateClaimStatusRequest request);
}
