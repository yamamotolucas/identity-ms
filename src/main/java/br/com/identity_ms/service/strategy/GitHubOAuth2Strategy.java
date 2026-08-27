package br.com.identity_ms.service.strategy;

import br.com.identity_ms.dto.output.OAuth2UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class GitHubOAuth2Strategy implements OAuth2ProviderStrategy {

    private final RestClient restClient;

    @Value("${oauth2.github.client-id}")
    private String clientId;

    @Value("${oauth2.github.client-secret}")
    private String clientSecret;

    @Value("${oauth2.github.redirect-uri}")
    private String redirectUri;

    public GitHubOAuth2Strategy(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public boolean supports(String provider) {
        return "GITHUB".equalsIgnoreCase(provider);
    }

    @Override
    public URI buildAuthorizationUrl() {
        return URI.create("https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=read:user,user:email");
    }

    @Override
    public OAuth2UserData authenticateAndFetchUser(String code) {
        Map<String, String> body = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "code", code,
                "redirect_uri", redirectUri
        );

        Map<?, ?> tokenResponse = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new IllegalArgumentException("Failed to retrieve access token from GitHub");
        }

        String accessToken = (String) tokenResponse.get("access_token");

        Map<?, ?> userInfoResponse = restClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        System.out.println(userInfoResponse);

        if (userInfoResponse == null) {
            throw new IllegalStateException("Failed to retrieve user profile from GitHub");
        }

        String providerId = String.valueOf(userInfoResponse.get("id"));
        String name = (String) userInfoResponse.get("name");
        String login = (String) userInfoResponse.get("login");
        String email = (String) userInfoResponse.get("email");

        if (email == null || email.isBlank()) {
            email = fetchPrimaryEmail(accessToken);
        }

        String finalName = (name != null && !name.isBlank()) ? name : login;

        return new OAuth2UserData(providerId, email, finalName);
    }

    private String fetchPrimaryEmail(String accessToken) {
        List<Map<String, Object>> emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (emails == null || emails.isEmpty()) {
            throw new IllegalStateException("No verified email found for GitHub user");
        }

        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")) && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElseGet(() -> (String) emails.get(0).get("email"));
    }
}