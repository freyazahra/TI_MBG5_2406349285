package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.requests.UpdateBudgetComponentRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.BudgetComponentResponse;
import apap.ti._2026.simbg_2406349285_be.models.BudgetComponent;

import java.util.List;

public interface BudgetComponentService {
    List<BudgetComponentResponse> getComponentsByAnggaranId(String anggaranId);
    BudgetComponentResponse getComponentById(String anggaranId, String id);
    BudgetComponent getComponentEntityById(String id);
    BudgetComponentResponse createComponent(String anggaranId, CreateBudgetComponentRequest request);
    BudgetComponentResponse updateComponent(String anggaranId, String id, UpdateBudgetComponentRequest request);
}
