package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.responses.SekolahResponse;
import apap.ti._2026.simbg_2406349285_be.services.SekolahService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SekolahController {

    private final SekolahService sekolahService;

    @GetMapping({"/sekolah", "/api/sekolah"})
    public ResponseEntity<BaseResponse<List<SekolahResponse>>> getAllSchools() {
        List<SekolahResponse> data = sekolahService.getAllSchools();
        return ResponseEntity.ok(BaseResponse.success(200, "Schools retrieved successfully", data));
    }
}
