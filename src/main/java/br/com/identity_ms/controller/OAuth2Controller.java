package br.com.identity_ms.controller;

import br.com.identity_ms.dto.output.TokenOutput;
import br.com.identity_ms.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "OAuth2 Authentication", description = "Unified endpoints for external social logins")
@RestController
@RequestMapping("/v1/auth/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oauth2Service;

    public OAuth2Controller(OAuth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @Operation(summary = "Redirect to OAuth2 Login", description = "Redirects the browser to Google or GitHub authentication screen.")
    @SecurityRequirements
    @GetMapping("/login/{provider}")
    public ResponseEntity<Void> redirectToProvider(@PathVariable String provider) {
        URI redirectUrl = oauth2Service.getAuthorizationUrl(provider);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl.toString())
                .build();
    }

    @Operation(summary = "OAuth2 Callback", description = "Receives authorization code from provider, validates credentials, and issues an application JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, token issued"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization code or provider", content = @Content)
    })
    @SecurityRequirements
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