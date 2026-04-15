package br.com.identity_ms.dto.output;

import br.com.identity_ms.model.user.UserEntity;

import java.time.LocalDateTime;

public record CreateUserOutput(
        String name,
        String email,
        LocalDateTime creationDate
) {
    public CreateUserOutput(UserEntity user) {
        this(
                user.getName(),
                user.getEmail(),
                user.getCreationDate()
        );
    }
}
