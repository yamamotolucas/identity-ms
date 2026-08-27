package br.com.identity_ms.service.strategy;

import br.com.identity_ms.dto.output.OAuth2UserData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import java.net.URI;
import java.util.Map;

@Component
public class GoogleOAuth2Strategy implements OAuth2ProviderStrategy {

    private final RestClient restClient;

    @Value("${oauth2.google.client-id}")
    private String clientId;

    @Value("${oauth2.google.client-secret}")
    private String clientSecret;

    @Value("${oauth2.google.redirect-uri}")
    private String redirectUri;

    public GoogleOAuth2Strategy(RestClient.Builder restClientBuilder) {this.restClient = restClientBuilder.build();}

    @Override
    public boolean supports(String provider) {return "GOOGLE".equalsIgnoreCase(provider);}

    @Override
    public URI buildAuthorizationUrl() {
        return URI.create("https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/userinfo.profile");
    }

    @Override
    public OAuth2UserData authenticateAndFetchUser(String code) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        Map<?, ?> tokenResponse = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(Map.class);

        String accessToken = (String) tokenResponse.get("access_token");

        Map<?, ?> userInfoResponse = restClient.get()
                .uri("https://www.googleapis.com/oauth2/v3/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        String providerId = (String) userInfoResponse.get("sub");
        String email = (String) userInfoResponse.get("email");
        String name = (String) userInfoResponse.get("name");

        return new OAuth2UserData(providerId, email, name);
    }
}