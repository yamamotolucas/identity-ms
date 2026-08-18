package br.com.identity_ms.controller;

import br.com.identity_ms.dto.output.BarberBaseOutput;
import br.com.identity_ms.dto.input.BarberUpdateInput;
import br.com.identity_ms.service.BarberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Barber", description = "Endpoints for managing and retrieving barber profiles and details")
@RestController
@RequestMapping("/v1/barber")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @Operation(summary = "Find all barbers", description = "Retrieves a complete list of all registered barbers.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of barbers retrieved successfully")})
    @SecurityRequirements
    @GetMapping("/findAll")
    public ResponseEntity<List<BarberBaseOutput>> findAll() {
        return ResponseEntity.ok(barberService.findAll());
    }

    @Operation(summary = "Get barber name by ID", description = "Retrieves the name of a specific barber using their ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Barber name retrieved successfully"), @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)})
    @SecurityRequirements
    @GetMapping("findById/{id}")
    public ResponseEntity<String> getBarberNameById(@PathVariable Long id) {
        return ResponseEntity.ok(barberService.getBarberNameById(id));
    }

    @Operation(summary = "Update barber profile", description = "Updates profile details for the authenticated barber identified by header.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Barber profile updated successfully"), @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content), @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)})
    @PatchMapping("/update")
    public ResponseEntity<BarberBaseOutput> update(
            @Valid @RequestBody BarberUpdateInput dto,
            @RequestHeader("X-User-Id") String barberId
    ) {
        return ResponseEntity.ok(barberService.update(dto, barberId));
    }

    @Operation(summary = "Get current barber profile", description = "Retrieves the profile details of the authenticated barber from the header ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Barber profile retrieved successfully"), @ApiResponse(responseCode = "404", description = "Barber not found", content = @Content)})
    @GetMapping("/me")
    public ResponseEntity<BarberBaseOutput> getBarberById(
            @RequestHeader("X-User-Id") String barberId
    ) {
        return ResponseEntity.ok(barberService.getBarberById(barberId));
    }
}