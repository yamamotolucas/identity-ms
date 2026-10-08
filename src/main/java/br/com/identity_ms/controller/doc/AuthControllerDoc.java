package br.com.identity_ms.controller.doc;

import br.com.identity_ms.dto.input.CreateUserInput;
import br.com.identity_ms.dto.input.LoginInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.TokenOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and token validation")
public interface AuthControllerDoc {

    @Operation(summary = "Register a new customer", description = "Creates a new user account with the CUSTOMER role and returns the created user details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or email already in use", content = @Content)
    })
    @SecurityRequirements
    ResponseEntity<CreateUserOutput> register(CreateUserInput input);

    @Operation(summary = "Authenticate user", description = "Validates user credentials (email and password) and generates a JWT Bearer access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input payload", content = @Content),
            @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content)
    })
    @SecurityRequirements
    ResponseEntity<TokenOutput> login(LoginInput input);

    @Operation(summary = "Validate JWT token", description = "Checks whether a provided JWT token is valid, active, and not expired.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Token is invalid, malformed, or expired", content = @Content)
    })
    @SecurityRequirements
    ResponseEntity<Map<String, String>> validate(@Parameter(description = "JWT Token to validate") String token);
}