package br.com.identity_ms.dto.input;

public interface UserCreateContract {
    String name();
    String email();
    String password();
    String confirmationPassword();
}
