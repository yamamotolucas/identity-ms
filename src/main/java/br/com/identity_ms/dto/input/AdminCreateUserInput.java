package br.com.identity_ms.dto.input;

import br.com.identity_ms.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUserInput(

    @NotBlank(message = "Email is required")
    @Email(message = "This field require a email address")
    String email,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Confirmation password is required")
    String confirmationPassword,

    @NotNull(message = "Role is required")
    UserRole role

) implements UserCreateContract {
}
