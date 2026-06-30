package br.com.identity_ms.controller;

import br.com.identity_ms.config.filters.token.TokenService;
import br.com.identity_ms.dto.input.CreateUserInput;
import br.com.identity_ms.dto.input.LoginInput;
import br.com.identity_ms.dto.output.TokenOutput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @PostMapping("/register")
    public ResponseEntity<CreateUserOutput> register(@RequestBody @Valid CreateUserInput input) {

        CreateUserOutput output = userService.create(input, UserRole.CUSTOMER);
        return ResponseEntity.ok().body(output);
    }

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
