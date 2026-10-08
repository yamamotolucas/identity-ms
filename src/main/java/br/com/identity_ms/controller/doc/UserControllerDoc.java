package br.com.identity_ms.controller.doc;

import br.com.identity_ms.dto.input.AdminCreateUserInput;
import br.com.identity_ms.dto.input.UserUpdateInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.UserBaseOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "Endpoints for managing user accounts and profiles")
public interface UserControllerDoc {

    @Operation(summary = "Create a user (Admin)", description = "Creates a new user account with a specified role by an administrator. **Required role:** `ADMIN`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or email already in use", content = @Content)
    })
    ResponseEntity<CreateUserOutput> create(AdminCreateUserInput input);

    @Operation(summary = "Get current user profile", description = "Retrieves the authenticated user details extracted from headers. **Required role:** `ADMIN`, `BARBER`, `CUSTOMER`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    })
    ResponseEntity<UserBaseOutput> getMe(
            @Parameter(hidden = true) String name,
            @Parameter(hidden = true) String email
    );

    @Operation(summary = "Update user profile", description = "Updates profile details for the authenticated user identified by header ID. **Required role:** `ADMIN`, `BARBER`, `CUSTOMER`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    ResponseEntity<UserBaseOutput> update(
            UserUpdateInput input,
            @Parameter(hidden = true) String userId
    );
}