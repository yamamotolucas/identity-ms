package br.com.identity_ms.controller;

import br.com.identity_ms.dto.output.BarberBaseOutput;
import br.com.identity_ms.model.barber.BarberEntity;
import br.com.identity_ms.repository.BarberRepository;
import br.com.identity_ms.service.BarberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/barber")
public class BarberController {

    private final BarberService barberService;

    public BarberController(BarberService barberService) {
        this.barberService = barberService;
    }

    @GetMapping
    public ResponseEntity<List<BarberBaseOutput>> findAll() {
        return ResponseEntity.ok(barberService.findAll());
    }
}
