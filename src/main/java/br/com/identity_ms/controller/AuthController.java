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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(output);

        return ResponseEntity.created(uri).body(output);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenOutput> login(@RequestBody @Valid LoginInput input) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.email(), input.password())
            );

            String token = tokenService.generateToken((UserEntity) authentication.getPrincipal());
            return ResponseEntity.ok(new TokenOutput(token));

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password.");
        }
    }

}
