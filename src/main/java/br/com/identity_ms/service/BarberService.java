package br.com.identity_ms.service;

import br.com.identity_ms.dto.output.BarberBaseOutput;
import br.com.identity_ms.model.barber.BarberEntity;
import br.com.identity_ms.repository.BarberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarberService {

    private final BarberRepository barberRepository;

    public BarberService(BarberRepository barberRepository) {
        this.barberRepository = barberRepository;
    }

    public List<BarberBaseOutput> findAll() {
        return barberRepository.findAll().stream().map(b -> new BarberBaseOutput(b.getUser())).toList();
    }
}
