package br.com.unasp.comandadigital.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "Recurso nao encontrado", ex.getMessage(), null, req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Regra de negocio", ex.getMessage(), null, req);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiError> handleStock(InsufficientStockException ex, HttpServletRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Estoque insuficiente", ex.getMessage(),
                ex.getDetalhes(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validacao", "Um ou mais campos sao invalidos", detalhes, req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<String> detalhes = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validacao", "Um ou mais campos sao invalidos", detalhes, req);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, "Conflito de dados",
                "Operacao viola uma restricao do banco (registro duplicado ou em uso)", null, req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado",
                "Voce nao tem permissao para este recurso", null, req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Nao autenticado", ex.getMessage(), null, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        // RNF-06: nunca expor stack trace para o cliente
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente.", null, req);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message,
                                           List<String> detalhes, HttpServletRequest req) {
        ApiError body = ApiError.of(status.value(), error, message, detalhes, req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
