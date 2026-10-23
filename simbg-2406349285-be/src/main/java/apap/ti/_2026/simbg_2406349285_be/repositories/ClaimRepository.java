package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, String> {
    List<Claim> findByAnggaranId(String anggaranId);
    List<Claim> findByStatus(String status);
}

