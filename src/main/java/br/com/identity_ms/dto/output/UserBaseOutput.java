package br.com.identity_ms.dto.output;

import br.com.identity_ms.model.user.UserEntity;

public record UserBaseOutput(
        String name,
        String email
) {
    public UserBaseOutput(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
