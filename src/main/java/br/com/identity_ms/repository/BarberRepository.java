package br.com.identity_ms.repository;

import br.com.identity_ms.model.barber.BarberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BarberRepository extends JpaRepository<BarberEntity, Long> {
}
