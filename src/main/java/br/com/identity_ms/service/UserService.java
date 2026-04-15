package br.com.identity_ms.service;

import br.com.identity_ms.dto.input.UserCreateContract;
import br.com.identity_ms.dto.output.CreateUserOutput;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        return new CreateUserOutput(userRepository.save(new UserEntity(input.name(), input.email(), hashedPassword, role)));

    }
}
