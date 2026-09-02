package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.CreateUserInput;
import br.com.identity_ms.dto.input.UserUpdateInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.UserBaseOutput;
import br.com.identity_ms.model.barber.BarberEntity;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should throw IllegalArgumentException when passwords do not match")
    public void shouldThrowExceptionWhenPasswordsDoNotMatch() {

        // Arrange
        CreateUserInput input = new CreateUserInput(
                "João Silva",
                "joao@email.com",
                "123456",
                "321654"
        );

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.create(input, UserRole.CUSTOMER)
        );

        // Assert
        assertEquals("Passwords don't match", exception.getMessage());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userRepository);

    }

    @Test
    @DisplayName("Should create customer user successfully when valid data is provided")
    public void shouldCreateCustomerSuccessfully() {
        // 1. ARRANGE
        CreateUserInput input = new CreateUserInput(
                "Maria Silva",
                "maria@email.com",
                "secret123",
                "secret123"
        );

        when(passwordEncoder.encode("secret123")).thenReturn("hashed_secret123");

        UserEntity savedUser = new UserEntity(
                input.name(),
                input.email(),
                "hashed_secret123",
                UserRole.CUSTOMER
        );
        savedUser.setId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // 2. ACT
        CreateUserOutput output = userService.create(input, UserRole.CUSTOMER);

        // 3. ASSERT
        assertNotNull(output);
        assertEquals(savedUser.getName(), output.name());
        assertEquals(savedUser.getEmail(), output.email());
        assertEquals(UserRole.CUSTOMER, savedUser.getRole());

        verify(passwordEncoder, times(1)).encode("secret123");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should create barber user with default barber profile successfully")
    public void shouldCreateBarberUserSuccessfully() {
        // 1. ARRANGE
        CreateUserInput input = new CreateUserInput(
                "carlos@email.com",
                "Carlos Barbeiro",
                "barber123",
                "barber123"
        );

        when(passwordEncoder.encode("barber123")).thenReturn("hashed_barber123");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        // 2. ACT
        CreateUserOutput output = userService.create(input, UserRole.BARBER);

        // 3. ASSERT
        assertNotNull(output);
        verify(userRepository).save(userCaptor.capture());
        UserEntity capturedUser = userCaptor.getValue();

        assertEquals("Carlos Barbeiro", capturedUser.getName());
        assertEquals("carlos@email.com", capturedUser.getEmail());
        assertEquals("hashed_barber123", capturedUser.getPassword());
        assertEquals(UserRole.BARBER, capturedUser.getRole());

        BarberEntity capturedBarber = capturedUser.getBarber();
        assertNotNull(capturedBarber, "Barber entity should not be null when role is BARBER");
        assertEquals("Cortes Clássicos", capturedBarber.getSpecialty());
        assertEquals(new BigDecimal("5.0"), capturedBarber.getRating());
        assertEquals("🧔", capturedBarber.getAvatar());
        assertEquals(capturedUser, capturedBarber.getUser(), "Bidirectional relationship should link barber to user");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user is not found by id")
    public void shouldThrowExceptionWhenUserNotFound() {
        //Arrange
        String userId = "99";
        Long parsedId = 99L;

        UserUpdateInput input = new UserUpdateInput(
                "Carlos Silva",
                null,
                null,
                null
        );

        when(userRepository.findById(parsedId)).thenReturn(Optional.empty());

        //Act
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.update(input, userId)
        );

        //Assert
        assertEquals("User not found with provided id", exception.getMessage());
        verify(userRepository, times(1)).findById(parsedId);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when new passwords do not match")
    public void shouldThrowIllegalArgumentExceptionWhenPasswordsDoNotMatch() {
        // Arrange
        String userId = "99";
        Long parsedId = 99L;

        UserUpdateInput input = new UserUpdateInput(
                "Carlos Silva",
                "currentPassword123",
                "newPassword123",
                "differentPassword456"
        );

        UserEntity savedUser = new UserEntity(
                "TestUser",
                "test@email.com",
                "hashed_secret123",
                UserRole.CUSTOMER
        );

        when(userRepository.findById(parsedId)).thenReturn(Optional.of(savedUser));

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(input, userId)
        );

        // Assert
        assertEquals("Passwords don't match", exception.getMessage());
        verify(userRepository, times(1)).findById(parsedId);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when current password is incorrect")
    public void shouldThrowIllegalArgumentExceptionWhenCurrentPasswordIsIncorrect() {
        // 1. ARRANGE
        String userId = "99";
        Long parsedId = 99L;

        UserUpdateInput input = new UserUpdateInput(
                "Carlos Silva",
                "wrongPassword",
                "newSecret123",
                "newSecret123"
        );

        UserEntity savedUser = new UserEntity(
                "TestUser",
                "test@email.com",
                "hashed_current_secret",
                UserRole.CUSTOMER
        );

        when(userRepository.findById(parsedId)).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("wrongPassword", "hashed_current_secret")).thenReturn(false);

        // 2. ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(input, userId)
        );

        // 3. ASSERT
        assertEquals("Current password is incorrect", exception.getMessage());
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashed_current_secret");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update user name successfully without changing password when password fields are null")
    public void shouldUpdateUserNameSuccessfullyWithoutChangingPassword() {
        // 1. ARRANGE
        String userId = "99";
        Long parsedId = 99L;

        UserUpdateInput input = new UserUpdateInput(
                "Novo Nome Atualizado",
                null,
                null,
                null
        );

        UserEntity savedUser = new UserEntity(
                "Nome Antigo",
                "carlos@email.com",
                "hashed_secret_original",
                UserRole.CUSTOMER
        );

        when(userRepository.findById(parsedId)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT
        UserBaseOutput output = userService.update(input, userId);

        // 3. ASSERT
        assertNotNull(output);
        assertEquals("Novo Nome Atualizado", output.name());
        assertEquals("carlos@email.com", output.email());
        assertEquals("hashed_secret_original", savedUser.getPassword());

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, times(1)).save(savedUser);
    }

    @Test
    @DisplayName("Should update both name and password successfully when valid data is provided")
    public void shouldUpdateNameAndPasswordSuccessfully() {
        // 1. ARRANGE
        String userId = "99";
        Long parsedId = 99L;

        UserUpdateInput input = new UserUpdateInput(
                "Carlos NewName",
                "oldSecret123",
                "newSecret456",
                "newSecret456"
        );

        UserEntity savedUser = new UserEntity(
                "Carlos old",
                "carlos@email.com",
                "hashed_oldSecret123",
                UserRole.CUSTOMER
        );

        when(userRepository.findById(parsedId)).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("oldSecret123", "hashed_oldSecret123")).thenReturn(true);
        when(passwordEncoder.encode("newSecret456")).thenReturn("hashed_newSecret456");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACT
        UserBaseOutput output = userService.update(input, userId);

        // 3. ASSERT
        assertNotNull(output);
        assertEquals("Carlos NewName", output.name());
        assertEquals("Carlos NewName", savedUser.getName());
        assertEquals("hashed_newSecret456", savedUser.getPassword());

        verify(passwordEncoder, times(1)).matches("oldSecret123", "hashed_oldSecret123");
        verify(passwordEncoder, times(1)).encode("newSecret456");
        verify(userRepository, times(1)).save(savedUser);
    }
}