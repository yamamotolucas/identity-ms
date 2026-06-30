package br.com.identity_ms.dto.output;

import br.com.identity_ms.model.user.UserRole;

public record TokenOutput(
        String token,
        UserRole role
) {
}
