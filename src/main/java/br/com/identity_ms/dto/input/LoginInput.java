package br.com.identity_ms.dto.input;

import jakarta.validation.constraints.NotBlank;

public record LoginInput(

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
