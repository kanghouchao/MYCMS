package com.cms.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class AuthExceptionHandlerTests {

  @Test
  void handleAuthentication_returnsUnauthorizedBody() {
    AuthExceptionHandler handler = new AuthExceptionHandler();
    AuthenticationException ex = mock(AuthenticationException.class);
    ResponseEntity<Map<String, Object>> resp = handler.handleAuthentication(ex);
    assertThat(resp.getStatusCode().value()).isEqualTo(401);
    assertThat(resp.getBody()).isInstanceOf(Map.class);
    Map<String, Object> body = Objects.requireNonNull(resp.getBody());
    assertThat(body.get("error")).isEqualTo("Invalid credentials");
  }

  @Test
  void handleValidation_returnsBadRequest_withFieldErrors() {
    AuthExceptionHandler handler = new AuthExceptionHandler();

    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(new Object(), "objectName");
    bindingResult.addError(new FieldError("objectName", "username", "must not be blank"));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<Map<String, Object>> resp = handler.handleValidation(ex);
    assertThat(resp.getStatusCode().value()).isEqualTo(400);
    Map<String, Object> body = Objects.requireNonNull(resp.getBody());
    assertThat(body.get("error")).isEqualTo("validation_failed");
    assertThat(body.get("details")).isInstanceOf(Map.class);
    @SuppressWarnings("unchecked")
    Map<String, String> details = (Map<String, String>) body.get("details");
    assertThat(details.get("username")).isEqualTo("must not be blank");
  }
}
