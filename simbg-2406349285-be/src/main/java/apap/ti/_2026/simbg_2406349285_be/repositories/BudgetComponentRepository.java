package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.BudgetComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetComponentRepository extends JpaRepository<BudgetComponent, String> {
    List<BudgetComponent> findByAnggaranId(String anggaranId);
}

