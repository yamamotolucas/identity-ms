package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.GithubEmails;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Service
public class GoogleLoginService {

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${google.secret}")
    private String googleSecret;

    private final RestClient restClient;

    public GoogleLoginService(RestClient.Builder restClient) {
        this.restClient = restClient.build();
    }

    public URI generateUrl() {
        return URI.create(
                "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&scope=https://www.googleapis.com/auth/userinfo.email" +
                "&response_type=code"
        );
    }

    private String getAcessToken(String code) {
           var response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "code", code,
                                "client_id", googleClientId,
                                "client_secret", googleSecret,
                                "redirect_uri", googleRedirectUri,
                                "grant_type", "authorization_code"
                        )
                )
                .retrieve()
                .body(Map.class);

           return response.get("id_token").toString();
    }

    public String getEmail(String code) {

        DecodedJWT jwt = JWT.decode(getAcessToken(code));
        return  jwt.getClaim("email").asString();

    }
}
