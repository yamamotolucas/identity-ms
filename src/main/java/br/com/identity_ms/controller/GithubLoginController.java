package br.com.identity_ms.controller;

import br.com.identity_ms.config.filters.token.TokenService;
import br.com.identity_ms.dto.output.TokenOutput;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.repository.UserRepository;
import br.com.identity_ms.service.GithubLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/login/github")
public class GithubLoginController {

    private final GithubLoginService service;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Value("${base.url.frontend}")
    private String frontendUrl;

    public GithubLoginController(GithubLoginService service, UserRepository userRepository, TokenService tokenService) {
        this.service = service;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<Void> redirect(){
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(service.generateUrl());
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/authorized")
    public ResponseEntity<Void> autenticateOAuth(@RequestParam("code") String code){

        HttpHeaders headers = new HttpHeaders();

        String email = service.getEmail(code);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        URI redirectUrl = URI.create(frontendUrl + "auth/oauth2/sucess?token=" + tokenService.generateToken(user) + "&role=" + user.getRole());
        headers.setLocation(redirectUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
