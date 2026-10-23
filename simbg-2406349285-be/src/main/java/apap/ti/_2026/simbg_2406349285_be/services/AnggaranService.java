package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateAnggaranRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.AnggaranResponse;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;

import java.util.List;

public interface AnggaranService {
    List<AnggaranResponse> getAllAnggaran();
    AnggaranResponse getAnggaranById(String id);
    Anggaran getAnggaranEntityById(String id);
    AnggaranResponse createAnggaran(CreateAnggaranRequest request);
}
