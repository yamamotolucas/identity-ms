package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.GithubEmails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

@Service
public class GithubLoginService {

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${github.secret}")
    private String githubSecret;

    private final RestClient restClient;

    public GithubLoginService(RestClient.Builder restClient) {
        this.restClient = restClient.build();
    }

    public URI generateUrl() {
        return URI.create(
                "https://github.com/login/oauth/authorize" +
                "?client_id=" + githubClientId +
                "&redirect_uri=" + githubRedirectUri +
                "&scope=read:user,user:email"
        );
    }

    private String getAcessToken(String code) {
           var response = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(
                        Map.of(
                                "code", code,
                                "client_id", githubClientId,
                                "client_secret", githubSecret,
                                "redirect_uri", githubRedirectUri
                        )
                )
                .retrieve()
                .body(Map.class);

           return response.get("access_token").toString();
    }

    public String getEmail(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(getAcessToken(code));

        var response = restClient.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(GithubEmails[].class);

        for(GithubEmails githubEmail : response){
            if(githubEmail.primary() && githubEmail.verified())
                return githubEmail.email();
        }

        return null;
    }
}
