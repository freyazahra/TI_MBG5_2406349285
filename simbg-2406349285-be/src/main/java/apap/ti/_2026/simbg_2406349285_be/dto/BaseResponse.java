package apap.ti._2026.simbg_2406349285_be.dto;

import lombok.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private int status;
    private String message;
    private String timestamp;
    private T data;

    public static <T> BaseResponse<T> success(int status, String message, T data) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(int status, String message) {
        return BaseResponse.<T>builder()
                .status(status)
                .message(message)
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .data(null)
                .build();
    }
}

