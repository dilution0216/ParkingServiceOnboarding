package org.dhicc.parkingserviceonboarding.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

//@RestControllerAdvice
public class GlobalExceptionHandler {

    // Swagger 관련 요청은 예외 처리에서 제외
    private boolean isSwaggerRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGlobalException(Exception ex, HttpServletRequest request) {
        if (isSwaggerRequest(request)) {
            // Checked Exception을 RuntimeException으로 변환 후 던지기
            throw new RuntimeException(ex);
        }

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "서버 내부 오류가 발생했습니다.");
        return errorResponse;
    }
}