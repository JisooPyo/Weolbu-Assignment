package weolbu.assignment.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import weolbu.assignment.common.dto.ApiResponseDto;

// 글로벌 예외 처리
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Controller 예외 핸들러
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ApiResponseDto> handlerCustomException(CustomException e) {
        return ResponseEntity
            .status(e.getCustomErrorCode().getStatusCode())
            .body(new ApiResponseDto(e.getMessage()));
    }

    // validation 예외 핸들러
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponseDto> handlerValicationException(MethodArgumentNotValidException ex) {
        ApiResponseDto apiResponseDto = null;
        for (FieldError fieldError : ex.getFieldErrors()) {
            apiResponseDto = new ApiResponseDto(fieldError.getDefaultMessage());
            break;
        }
        return ResponseEntity.badRequest().body(apiResponseDto);
    }
}
