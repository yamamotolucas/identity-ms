package br.com.identity_ms.controller;

import br.com.identity_ms.config.filters.token.TokenService;
import br.com.identity_ms.dto.input.CreateUserInput;
import br.com.identity_ms.dto.input.LoginInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.TokenOutput;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, and token validation")
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Register a new customer", description = "Creates a new user account with the CUSTOMER role and returns the created user details.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User successfully registered"), @ApiResponse(responseCode = "400", description = "Invalid payload or email already in use", content = @Content)})
    @SecurityRequirements
    @PostMapping("/register")
    public ResponseEntity<CreateUserOutput> register(@RequestBody @Valid CreateUserInput input) {
        CreateUserOutput output = userService.create(input, UserRole.CUSTOMER);
        return ResponseEntity.ok().body(output);
    }

    @Operation(summary = "Authenticate user", description = "Validates user credentials (email and password) and generates a JWT Bearer access token.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authentication successful, token returned"), @ApiResponse(responseCode = "400", description = "Invalid input payload", content = @Content), @ApiResponse(responseCode = "401", description = "Invalid email or password", content = @Content)})
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<TokenOutput> login(@RequestBody @Valid LoginInput input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.email(), input.password())
            );

            UserEntity user = (UserEntity) authentication.getPrincipal();

            String token = tokenService.generateToken(user);
            return ResponseEntity.ok(new TokenOutput(token, user.getRole()));

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password.");
        }
    }

    @Operation(summary = "Validate JWT token", description = "Checks whether a provided JWT token is valid, active, and not expired.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Token is valid"), @ApiResponse(responseCode = "401", description = "Token is invalid, malformed, or expired", content = @Content)})
    @SecurityRequirements
    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validate(@RequestParam String token) {
        String tokenResponse = tokenService.decodeToken(token);
        if (tokenResponse == null || tokenResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token expired or invalid"));
        }

        return ResponseEntity.ok(Map.of("result", "valid"));
    }
}