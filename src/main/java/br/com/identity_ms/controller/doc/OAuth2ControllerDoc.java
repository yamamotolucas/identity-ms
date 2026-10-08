package br.com.identity_ms.controller.doc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "OAuth2 Authentication", description = "Unified endpoints for external social logins")
public interface OAuth2ControllerDoc {

    @Operation(summary = "Redirect to OAuth2 Login", description = "Redirects the browser to Google or GitHub authentication screen.")
    @SecurityRequirements
    ResponseEntity<Void> redirectToProvider(
            @Parameter(description = "OAuth2 Provider (e.g. google, github)") String provider
    );

    @Operation(summary = "OAuth2 Callback", description = "Receives authorization code from provider, validates credentials, and issues an application JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, token issued"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization code or provider", content = @Content)
    })
    @SecurityRequirements
    ResponseEntity<Void> callback(
            @Parameter(description = "OAuth2 Provider (e.g. google, github)") String provider,
            @Parameter(description = "Authorization code issued by provider") String code
    );
}