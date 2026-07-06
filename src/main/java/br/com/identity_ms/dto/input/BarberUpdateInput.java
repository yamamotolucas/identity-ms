package br.com.identity_ms.dto.input;

import java.math.BigDecimal;

public record BarberUpdateInput(
        String specialty,
        String avatar
) {
}
