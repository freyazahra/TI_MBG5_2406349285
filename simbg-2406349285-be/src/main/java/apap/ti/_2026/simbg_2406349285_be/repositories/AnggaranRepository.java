package apap.ti._2026.simbg_2406349285_be.repositories;

import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnggaranRepository extends JpaRepository<Anggaran, String> {
    List<Anggaran> findBySekolahId(String sekolahId);
}

