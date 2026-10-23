package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.responses.SekolahResponse;
import apap.ti._2026.simbg_2406349285_be.models.Sekolah;

import java.util.List;

public interface SekolahService {
    List<SekolahResponse> getAllSchools();
    Sekolah getSchoolById(String id);
}
