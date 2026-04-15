package br.com.identity_ms.dto.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserInput(

        @NotBlank(message = "Email is required")
        @Email(message = "This field require a email address")
        String email,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirmation password is required")
        String confirmationPassword

) implements UserCreateContract{
}
