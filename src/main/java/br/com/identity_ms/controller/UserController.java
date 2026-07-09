package br.com.identity_ms.controller;

import br.com.identity_ms.dto.input.AdminCreateUserInput;
import br.com.identity_ms.dto.input.UserUpdateInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.UserBaseOutput;
import br.com.identity_ms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/me")
    public ResponseEntity<UserBaseOutput> getMe(
            @RequestHeader("X-User-Name") String name,
            @RequestHeader("X-User-Email") String email
    ) {
        return ResponseEntity.ok().body(new UserBaseOutput(name, email));
    }

    @PatchMapping("/update")
    public ResponseEntity<UserBaseOutput> update(@RequestBody UserUpdateInput input, @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(userService.update(input, userId));
    }
}
