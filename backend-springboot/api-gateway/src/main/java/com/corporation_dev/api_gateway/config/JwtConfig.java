package com.corporation_dev.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;

import com.corporation_dev.api_gateway.security.AudienceValidator;

@Configuration
public class JwtConfig {
    @Bean
    public ReactiveJwtDecoder jwtDecoder(AudienceValidator audienceValidator) {

        NimbusReactiveJwtDecoder jwtDecoder =
                (NimbusReactiveJwtDecoder) ReactiveJwtDecoders.fromIssuerLocation(
                        "https://dev-8yt6f555y2l6kaad.us.auth0.com/"
                );

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefault();

        OAuth2TokenValidator<Jwt> withAudience =
                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

}
