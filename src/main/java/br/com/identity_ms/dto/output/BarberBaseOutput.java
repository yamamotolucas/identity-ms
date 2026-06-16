package br.com.identity_ms.dto.output;

import br.com.identity_ms.model.user.UserEntity;

import java.math.BigDecimal;

public record BarberBaseOutput(
    Long id,
    String name,
    String email,
    String specialty,
    BigDecimal rating,
    String avatar
) {
    public BarberBaseOutput(UserEntity user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBarber().getSpecialty(),
                user.getBarber().getRating(),
                user.getBarber().getAvatar()
        );
    }
}
