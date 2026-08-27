package br.com.identity_ms.dto.output;

public record OAuth2UserData(
        String providerId,
        String email,
        String name
) {}