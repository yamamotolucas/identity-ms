package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.UserCreateContract;
import br.com.identity_ms.dto.input.UserUpdateInput;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.dto.output.OAuth2UserData;
import br.com.identity_ms.dto.output.UserBaseOutput;
import br.com.identity_ms.model.barber.BarberEntity;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateUserOutput create(UserCreateContract input, UserRole role) {

        if(!input.password().equals(input.confirmationPassword()))
            throw new IllegalArgumentException("Passwords don't match");

        String hashedPassword = passwordEncoder.encode(input.password());

        UserEntity user = new UserEntity(input.name(), input.email(), hashedPassword, role);

        if(role == UserRole.BARBER) {
            BarberEntity barber = new BarberEntity();
            barber.setSpecialty("Cortes Clássicos");
            barber.setRating(new BigDecimal("5.0"));
            barber.setAvatar("🧔");
            user.setBarber(barber);
        }

        return new CreateUserOutput(userRepository.save(user));
    }

    @Transactional
    public UserEntity findOrCreateSocialUser(OAuth2UserData userData) {
        return userRepository.findByEmail(userData.email())
                .orElseGet(() -> {
                    String randomPassword = UUID.randomUUID().toString();
                    String hashedPassword = passwordEncoder.encode(randomPassword);

                    UserEntity newUser = new UserEntity(
                            userData.name(),
                            userData.email(),
                            hashedPassword,
                            UserRole.CUSTOMER
                    );

                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public UserBaseOutput update(UserUpdateInput input, String userId) {

        UserEntity savedUser = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new EntityNotFoundException("User not found with provided id"));

        if (input.name() != null) {
            savedUser.setName(input.name());
        }

        if(input.newPassword() != null && input.confirmationNewPassword() != null && input.password() != null) {
            if(!input.newPassword().equals(input.confirmationNewPassword()))
                throw new IllegalArgumentException("Passwords don't match");

            if(!passwordEncoder.matches(input.password(), savedUser.getPassword()))
                throw new IllegalArgumentException("Current password is incorrect");

            savedUser.setPassword(passwordEncoder.encode(input.newPassword()));
        }

        UserEntity updatedUser = userRepository.save(savedUser);
        return new UserBaseOutput(updatedUser.getName(), updatedUser.getEmail());
    }
}