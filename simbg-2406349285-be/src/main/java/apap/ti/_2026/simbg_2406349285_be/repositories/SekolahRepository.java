package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.Sekolah;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SekolahRepository extends JpaRepository<Sekolah, String> {
}

