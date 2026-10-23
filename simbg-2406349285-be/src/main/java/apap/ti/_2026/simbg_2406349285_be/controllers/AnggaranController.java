package apap.ti._2026.simbg_2406349285_be.controllers;

import apap.ti._2026.simbg_2406349285_be.dto.BaseResponse;
import apap.ti._2026.simbg_2406349285_be.dto.requests.CreateAnggaranRequest;
import apap.ti._2026.simbg_2406349285_be.dto.responses.AnggaranResponse;
import apap.ti._2026.simbg_2406349285_be.services.AnggaranService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnggaranController {

    private final AnggaranService anggaranService;

    @GetMapping({"/anggaran", "/api/anggaran"})
    public ResponseEntity<BaseResponse<List<AnggaranResponse>>> getAllAnggaran() {
        List<AnggaranResponse> data = anggaranService.getAllAnggaran();
        return ResponseEntity.ok(BaseResponse.success(200, "Anggaran retrieved successfully", data));
    }

    @PostMapping({"/anggaran", "/api/anggaran"})
    public ResponseEntity<BaseResponse<AnggaranResponse>> createAnggaran(@Valid @RequestBody CreateAnggaranRequest request) {
        AnggaranResponse data = anggaranService.createAnggaran(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(201, "Anggaran created successfully", data));
    }

    @GetMapping({"/anggaran/{id}", "/api/anggaran/{id}"})
    public ResponseEntity<BaseResponse<AnggaranResponse>> getAnggaranById(@PathVariable("id") String id) {
        AnggaranResponse data = anggaranService.getAnggaranById(id);
        return ResponseEntity.ok(BaseResponse.success(200, "Anggaran retrieved successfully", data));
    }
}
