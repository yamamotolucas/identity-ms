package br.com.identity_ms.service;

import br.com.identity_ms.config.filters.token.TokenService;
import br.com.identity_ms.dto.output.OAuth2UserData;
import br.com.identity_ms.dto.output.TokenOutput;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.service.strategy.OAuth2ProviderStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class OAuth2Service {

    private final List<OAuth2ProviderStrategy> strategies;
    private final UserService userService;
    private final TokenService tokenService;

    @Value("${base.url.frontend}")
    private String frontendUrl;

    public OAuth2Service(List<OAuth2ProviderStrategy> strategies, UserService userService, TokenService tokenService) {
        this.strategies = strategies;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public URI getAuthorizationUrl(String provider) {
        return findStrategy(provider).buildAuthorizationUrl();
    }

    public URI handleCallback(String provider, String code) {

        OAuth2ProviderStrategy strategy = findStrategy(provider);
        OAuth2UserData userData = strategy.authenticateAndFetchUser(code);
        UserEntity user = userService.findOrCreateSocialUser(userData);

        return URI.create(frontendUrl + "auth/oauth2/sucess?token=" + tokenService.generateToken(user) + "&role=" + user.getRole());

    }

    private OAuth2ProviderStrategy findStrategy(String provider) {
        return strategies.stream()
                .filter(s -> s.supports(provider))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported OAuth2 provider: " + provider));
    }
}