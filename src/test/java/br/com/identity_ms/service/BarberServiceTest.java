package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.BarberUpdateInput;
import br.com.identity_ms.dto.output.BarberBaseOutput;
import br.com.identity_ms.model.barber.BarberEntity;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.repository.BarberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BarberService Unit Tests")
public class BarberServiceTest {

    @Mock
    private BarberRepository barberRepository;

    @InjectMocks
    private BarberService barberService;

    private BarberEntity createFakeBarber(Long userId, String name, String specialty, String avatar) {
        UserEntity user = new UserEntity(name, "barber@email.com", "hashed_pwd", UserRole.BARBER);
        user.setId(userId);
        BarberEntity barber = new BarberEntity();
        barber.setUserId(userId);
        barber.setSpecialty(specialty);
        barber.setRating(new BigDecimal("5.0"));
        barber.setAvatar(avatar);
        user.setBarber(barber);
        return barber;
    }

    @Test
    @DisplayName("Should return list of BarberBaseOutput when barbers exist")
    public void shouldReturnAllBarbersSuccessfully() {
        // Arrange
        BarberEntity barber1 = createFakeBarber(1L, "Barbeiro Um", "Degradê", "💈");
        BarberEntity barber2 = createFakeBarber(2L, "Barbeiro Dois", "Barba Terapia", "✂️");

        when(barberRepository.findAll()).thenReturn(List.of(barber1, barber2));

        // Act
        List<BarberBaseOutput> result = barberService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(barberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no barbers exist")
    public void shouldReturnEmptyListWhenNoBarbersExist() {
        // Arrange
        when(barberRepository.findAll()).thenReturn(List.of());

        // Act
        List<BarberBaseOutput> result = barberService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(barberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return barber name when barber exists by user id")
    public void shouldReturnBarberNameWhenBarberExists() {
        // Arrange
        Long userId = 1L;
        BarberEntity barber = createFakeBarber(userId, "Carlos Barbeiro", "Cortes Clássicos", "🧔");
        when(barberRepository.findByUserId(userId)).thenReturn(Optional.of(barber));

        // Act
        String barberName = barberService.getBarberNameById(userId);

        // Assert
        assertEquals("Carlos Barbeiro", barberName);
        verify(barberRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when barber name is requested and barber not found")
    public void shouldThrowExceptionWhenBarberNameNotFound() {
        // Arrange
        Long userId = 1L;
        when(barberRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> barberService.getBarberNameById(userId)
        );

        // Assert
        assertEquals("Barber not found with provided id", exception.getMessage());
        verify(barberRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent barber")
    public void shouldThrowExceptionWhenUpdatingNonExistentBarber() {
        // Arrange
        String barberId = "1";
        BarberUpdateInput input = new BarberUpdateInput("Degradê Moderno", "🧔‍♂️");
        when(barberRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> barberService.update(input, barberId)
        );

        assertEquals("Barber not found with provided id", exception.getMessage());
        verify(barberRepository, times(1)).findByUserId(1L);
        verify(barberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update specialty and avatar successfully when valid data is provided")
    public void shouldUpdateSpecialtyAndAvatarSuccessfully() {
        // Arrange
        String barberId = "1";
        BarberUpdateInput input = new BarberUpdateInput("Degradê Navalhado", "💈");
        BarberEntity existingBarber = createFakeBarber(1L, "Carlos Barbeiro", "Cortes Clássicos", "🧔");

        when(barberRepository.findByUserId(1L)).thenReturn(Optional.of(existingBarber));
        when(barberRepository.save(any(BarberEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BarberBaseOutput output = barberService.update(input, barberId);

        // Assert
        assertNotNull(output);
        assertEquals("Degradê Navalhado", existingBarber.getSpecialty());
        assertEquals("💈", existingBarber.getAvatar());
        verify(barberRepository, times(1)).findByUserId(1L);
        verify(barberRepository, times(1)).save(existingBarber);
    }

    @Test
    @DisplayName("Should not update fields when input fields are null")
    public void shouldNotUpdateFieldsWhenInputFieldsAreNull() {
        // Arrange
        String barberId = "1";
        BarberUpdateInput input = new BarberUpdateInput(null, null);
        BarberEntity existingBarber = createFakeBarber(1L, "Carlos Barbeiro", "Cortes Clássicos", "🧔");

        when(barberRepository.findByUserId(1L)).thenReturn(Optional.of(existingBarber));
        when(barberRepository.save(any(BarberEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        BarberBaseOutput output = barberService.update(input, barberId);

        // Assert
        assertNotNull(output);
        assertEquals("Cortes Clássicos", existingBarber.getSpecialty());
        assertEquals("🧔", existingBarber.getAvatar());
        verify(barberRepository, times(1)).save(existingBarber);
    }

    @Test
    @DisplayName("Should return BarberBaseOutput when barber exists by id")
    public void shouldReturnBarberByIdSuccessfully() {
        // Arrange
        String barberId = "1";
        BarberEntity barber = createFakeBarber(1L, "Carlos Barbeiro", "Cortes Clássicos", "🧔");
        when(barberRepository.findByUserId(1L)).thenReturn(Optional.of(barber));

        // Act
        BarberBaseOutput output = barberService.getBarberById(barberId);

        // Assert
        assertNotNull(output);
        verify(barberRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getBarberById does not find barber")
    public void shouldThrowExceptionWhenGetBarberByIdNotFound() {
        // Arrange
        String barberId = "1";
        when(barberRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> barberService.getBarberById(barberId)
        );

        // Assert

        assertEquals("Barber not found with provided id", exception.getMessage());
        verify(barberRepository, times(1)).findByUserId(1L);
    }
}