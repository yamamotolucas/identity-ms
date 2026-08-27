package br.com.identity_ms.controller;

import br.com.identity_ms.dto.input.AdminCreateUserInput;
import br.com.identity_ms.dto.input.UserUpdateInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.UserBaseOutput;
import br.com.identity_ms.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Users", description = "Endpoints for managing user accounts and profiles")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a user (Admin)", description = "Creates a new user account with a specified role by an administrator. **Required role:** `ADMIN`")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "User created successfully"), @ApiResponse(responseCode = "400", description = "Invalid payload or email already in use", content = @Content)})
    @PostMapping
    public ResponseEntity<CreateUserOutput> create(@RequestBody @Valid AdminCreateUserInput input) {

        CreateUserOutput output = userService.create(input, input.role());

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .build(output);

        return ResponseEntity.created(uri).body(output);
    }

    @Operation(summary = "Get current user profile", description = "Retrieves the authenticated user details extracted from headers. **Required role:** `ADMIN`, `BARBER`, `CUSTOMER`")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User profile retrieved successfully")})
    @GetMapping("/me")
    public ResponseEntity<UserBaseOutput> getMe(
            @Parameter(hidden = true)
            @RequestHeader("X-User-Name") String name,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Email") String email
    ) {
        return ResponseEntity.ok().body(new UserBaseOutput(name, email));
    }

    @Operation(summary = "Update user profile", description = "Updates profile details for the authenticated user identified by header ID. **Required role:** `ADMIN`, `BARBER`, `CUSTOMER`")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User updated successfully"), @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content), @ApiResponse(responseCode = "404", description = "User not found", content = @Content)})
    @PatchMapping("/update")
    public ResponseEntity<UserBaseOutput> update(
            @RequestBody UserUpdateInput input,
            @Parameter(hidden = true)
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(userService.update(input, userId));
    }
}