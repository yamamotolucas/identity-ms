package br.com.identity_ms.controller;

import br.com.identity_ms.dto.output.BarberBaseOutput;
import br.com.identity_ms.dto.input.BarberUpdateInput;
import br.com.identity_ms.service.BarberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/barber")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<BarberBaseOutput>> findAll() {
        return ResponseEntity.ok(barberService.findAll());
    }

    @GetMapping("findById/{id}")
    public ResponseEntity<String> getBarberNameById(@PathVariable Long id) {
        return ResponseEntity.ok(barberService.getBarberNameById(id));
    }

    @PatchMapping("/update")
    public ResponseEntity<BarberBaseOutput> update(
            @Valid @RequestBody BarberUpdateInput dto,
            @RequestHeader("X-User-Id") String barberId
    ) {
        return ResponseEntity.ok(barberService.update(dto, barberId));
    }

    @GetMapping("/me")
    public ResponseEntity<BarberBaseOutput> getBarberById(
            @RequestHeader("X-User-Id") String barberId
    ) {
        return ResponseEntity.ok(barberService.getBarberById(barberId));
    }
}
