package br.com.identity_ms.controller;

import br.com.identity_ms.dto.input.AdminCreateUserInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreateUserOutput> create(@RequestBody @Valid AdminCreateUserInput input) {

        CreateUserOutput output = userService.create(input, input.role());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(output);

        return ResponseEntity.created(uri).body(output);
    }



}
