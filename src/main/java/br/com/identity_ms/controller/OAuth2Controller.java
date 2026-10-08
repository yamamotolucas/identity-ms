package br.com.identity_ms.controller;

import br.com.identity_ms.controller.doc.OAuth2ControllerDoc;
import br.com.identity_ms.service.OAuth2Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/v1/auth/oauth2")
public class OAuth2Controller implements OAuth2ControllerDoc {

    private final OAuth2Service oauth2Service;

    public OAuth2Controller(OAuth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @Override
    @GetMapping("/login/{provider}")
    public ResponseEntity<Void> redirectToProvider(@PathVariable String provider) {
        URI redirectUrl = oauth2Service.getAuthorizationUrl(provider);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl.toString())
                .build();
    }

    @Override
    @GetMapping("/callback/{provider}")
    public ResponseEntity<Void> callback(
            @PathVariable String provider,
            @RequestParam("code") String code
    ) {
        HttpHeaders headers = new HttpHeaders();
        URI redirectUrl = oauth2Service.handleCallback(provider, code);
        headers.setLocation(redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}