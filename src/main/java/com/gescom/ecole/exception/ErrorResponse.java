package com.gescom.ecole.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
    
    // Builder manuel
    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }
    
    public static class ErrorResponseBuilder {
        private ErrorResponse response = new ErrorResponse();
        
        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            response.timestamp = timestamp;
            return this;
        }
        
        public ErrorResponseBuilder status(Integer status) {
            response.status = status;
            return this;
        }
        
        public ErrorResponseBuilder error(String error) {
            response.error = error;
            return this;
        }
        
        public ErrorResponseBuilder message(String message) {
            response.message = message;
            return this;
        }
        
        public ErrorResponseBuilder path(String path) {
            response.path = path;
            return this;
        }
        
        public ErrorResponseBuilder validationErrors(Map<String, String> validationErrors) {
            response.validationErrors = validationErrors;
            return this;
        }
        
        public ErrorResponse build() {
            return response;
        }
    }
}
