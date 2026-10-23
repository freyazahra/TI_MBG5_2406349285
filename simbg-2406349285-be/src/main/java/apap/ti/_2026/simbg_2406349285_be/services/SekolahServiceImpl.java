package apap.ti._2026.simbg_2406349285_be.services;

import apap.ti._2026.simbg_2406349285_be.dto.responses.SekolahResponse;
import apap.ti._2026.simbg_2406349285_be.exceptions.ResourceNotFoundException;
import apap.ti._2026.simbg_2406349285_be.models.Anggaran;
import apap.ti._2026.simbg_2406349285_be.models.Sekolah;
import apap.ti._2026.simbg_2406349285_be.repositories.AnggaranRepository;
import apap.ti._2026.simbg_2406349285_be.repositories.SekolahRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SekolahServiceImpl implements SekolahService {

    private final SekolahRepository sekolahRepository;
    private final AnggaranRepository anggaranRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SekolahResponse> getAllSchools() {
        return sekolahRepository.findAll().stream().map(sekolah -> {
            List<Anggaran> activeAnggaran = anggaranRepository.findBySekolahId(sekolah.getId()).stream()
                    .filter(a -> "AKTIF".equalsIgnoreCase(a.getStatus()))
                    .toList();
            long activeAnggaranCount = activeAnggaran.size();
            long activeAnggaranTotal = activeAnggaran.stream()
                    .mapToLong(a -> a.getTotalAnggaran() != null ? a.getTotalAnggaran() : 0L)
                    .sum();

            return SekolahResponse.builder()
                    .id(sekolah.getId())
                    .nama(sekolah.getNama())
                    .educationLevel(sekolah.getEducationLevel())
                    .address(sekolah.getAddress())
                    .createdAt(sekolah.getCreatedAt())
                    .updatedAt(sekolah.getUpdatedAt())
                    .activeAnggaranCount(activeAnggaranCount)
                    .activeAnggaranTotal(activeAnggaranTotal)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Sekolah getSchoolById(String id) {
        return sekolahRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sekolah tidak ditemukan: " + id));
    }
}
