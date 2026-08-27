package br.com.identity_ms.service.strategy;

import br.com.identity_ms.dto.output.OAuth2UserData;
import java.net.URI;

public interface OAuth2ProviderStrategy {

    boolean supports(String provider);

    URI buildAuthorizationUrl();

    OAuth2UserData authenticateAndFetchUser(String code);
    
}
