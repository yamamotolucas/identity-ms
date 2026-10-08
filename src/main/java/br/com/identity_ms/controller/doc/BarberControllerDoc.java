package br.com.identity_ms.controller.doc;

import br.com.identity_ms.dto.input.BarberUpdateInput;
import br.com.identity_ms.dto.output.BarberBaseOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Barber", description = "Endpoints for managing and retrieving barber profiles and details")
public interface BarberControllerDoc {

    @Operation(summary = "Find all barbers", description = "Retrieves a complete list of all registered barbers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of barbers retrieved successfully")
    })
    @SecurityRequirements
    ResponseEntity<List<BarberBaseOutput>> findAll();

    @Operation(summary = "Get barber name by ID", description = "Retrieves the name of a specific barber using their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Barber name retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)
    })
    @SecurityRequirements
    ResponseEntity<String> getBarberNameById(Long id);

    @Operation(summary = "Update barber profile", description = "Updates profile details for the authenticated barber identified by header. **Required role:** `BARBER`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Barber profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)
    })
    ResponseEntity<BarberBaseOutput> update(
            BarberUpdateInput dto,
            @Parameter(hidden = true) String barberId
    );

    @Operation(summary = "Get current barber profile", description = "Retrieves the profile details of the authenticated barber from the header ID. **Required role:** `BARBER`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Barber profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)
    })
    ResponseEntity<BarberBaseOutput> getBarberById(
            @Parameter(hidden = true) String barberId
    );
}