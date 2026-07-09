package br.com.identity_ms.dto.input;

public record UserUpdateInput(
        String name,
        String password,
        String newPassword,
        String confirmationNewPassword
) {
}
