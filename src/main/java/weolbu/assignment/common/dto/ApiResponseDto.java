package weolbu.assignment.common.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto {
    private String message;
    private Object data;

    public ApiResponseDto(String message) {
        this.message = message;
    }

    /**
     * @param message Response body 에 담을 메시지
     * @param data Response body 에 담을 data
     */
    public ApiResponseDto(String message, Object data) {
        this.message = message;
        this.data = data;
    }
}
