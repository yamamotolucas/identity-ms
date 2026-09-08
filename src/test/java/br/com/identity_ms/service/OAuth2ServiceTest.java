package br.com.identity_ms.service;

import br.com.identity_ms.config.filters.token.TokenService;
import br.com.identity_ms.dto.output.OAuth2UserData;
import br.com.identity_ms.model.user.UserEntity;
import br.com.identity_ms.model.user.UserRole;
import br.com.identity_ms.service.strategy.OAuth2ProviderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2Service Unit Tests")
public class OAuth2ServiceTest {

    @Mock
    private OAuth2ProviderStrategy gitHubStrategy;

    @Mock
    private OAuth2ProviderStrategy googleStrategy;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    private OAuth2Service oAuth2Service;

    private final String frontendUrl = "http://localhost:3000/";

    @BeforeEach
    void setUp() {

        oAuth2Service = new OAuth2Service(
                List.of(gitHubStrategy, googleStrategy),
                userService,
                tokenService
        );

        ReflectionTestUtils.setField(oAuth2Service, "frontendUrl", frontendUrl);
    }

    @Test
    @DisplayName("Should return authorization URI when supported provider is passed")
    public void shouldReturnAuthorizationUriWhenProviderIsSupported() {
        // Arrange
        String provider = "GITHUB";
        URI expectedUri = URI.create("https://github.com/login/oauth/authorize?client_id=123");

        when(gitHubStrategy.supports(provider)).thenReturn(true);
        when(gitHubStrategy.buildAuthorizationUrl()).thenReturn(expectedUri);

        // Act
        URI resultUri = oAuth2Service.getAuthorizationUrl(provider);

        // Assert
        assertNotNull(resultUri);
        assertEquals(expectedUri, resultUri);
        verify(gitHubStrategy, times(1)).supports(provider);
        verify(gitHubStrategy, times(1)).buildAuthorizationUrl();
        verifyNoInteractions(googleStrategy, userService, tokenService);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unsupported provider is passed to getAuthorizationUrl")
    public void shouldThrowExceptionWhenProviderIsUnsupportedForAuthorizationUrl() {
        // Arrange
        String provider = "FACEBOOK";

        when(gitHubStrategy.supports(provider)).thenReturn(false);
        when(googleStrategy.supports(provider)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> oAuth2Service.getAuthorizationUrl(provider)
        );

        assertEquals("Unsupported OAuth2 provider: FACEBOOK", exception.getMessage());
        verify(gitHubStrategy, times(1)).supports(provider);
        verify(googleStrategy, times(1)).supports(provider);
        verifyNoInteractions(userService, tokenService);
    }

    @Test
    @DisplayName("Should handle callback successfully and return frontend redirect URI with token and role")
    public void shouldHandleCallbackSuccessfullyAndReturnRedirectUri() {
        // Arrange
        String provider = "GOOGLE";
        String authCode = "valid_auth_code";
        OAuth2UserData userData = new OAuth2UserData("google-sub-123", "client@email.com", "Carlos Cliente");

        UserEntity user = new UserEntity(
                "Carlos Cliente",
                "client@email.com",
                "random_pass",
                UserRole.CUSTOMER
        );

        when(gitHubStrategy.supports(provider)).thenReturn(false);
        when(googleStrategy.supports(provider)).thenReturn(true);
        when(googleStrategy.authenticateAndFetchUser(authCode)).thenReturn(userData);
        when(userService.findOrCreateSocialUser(userData)).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("jwt_mocked_token");

        // Act
        URI resultUri = oAuth2Service.handleCallback(provider, authCode);

        // Assert
        assertNotNull(resultUri);
        String expectedRedirect = frontendUrl + "auth/oauth2/sucess?token=jwt_mocked_token&role=CUSTOMER";
        assertEquals(URI.create(expectedRedirect), resultUri);

        verify(googleStrategy, times(1)).authenticateAndFetchUser(authCode);
        verify(userService, times(1)).findOrCreateSocialUser(userData);
        verify(tokenService, times(1)).generateToken(user);
        verify(gitHubStrategy, never()).authenticateAndFetchUser(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when unsupported provider is passed to handleCallback")
    public void shouldThrowExceptionWhenProviderIsUnsupportedForCallback() {
        // Arrange
        String provider = "TWITTER";
        String authCode = "any_code";

        when(gitHubStrategy.supports(provider)).thenReturn(false);
        when(googleStrategy.supports(provider)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> oAuth2Service.handleCallback(provider, authCode)
        );

        assertEquals("Unsupported OAuth2 provider: TWITTER", exception.getMessage());
        verify(gitHubStrategy, times(1)).supports(provider);
        verify(googleStrategy, times(1)).supports(provider);
        verifyNoInteractions(userService, tokenService);
    }
}