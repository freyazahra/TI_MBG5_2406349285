package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.ClaimItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimItemRepository extends JpaRepository<ClaimItem, String> {
    List<ClaimItem> findByClaimId(String claimId);
    List<ClaimItem> findByBudgetComponentId(String budgetComponentId);
}

