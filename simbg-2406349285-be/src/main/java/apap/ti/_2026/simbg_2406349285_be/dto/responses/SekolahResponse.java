package apap.ti._2026.simbg_2406349285_be.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SekolahResponse {
    private String id;
    private String nama;
    private String educationLevel;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long activeAnggaranCount;
}

