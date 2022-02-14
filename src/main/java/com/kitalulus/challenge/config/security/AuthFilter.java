package com.kitalulus.challenge.config.security;

import com.kitalulus.challenge.config.security.context.SecurityContextHolder;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AuthFilter implements HandlerInterceptor {

    private final SecurityContextHolder securityContextHolder;

    @Autowired
    public AuthFilter(SecurityContextHolder securityContextHolder) {
        this.securityContextHolder = securityContextHolder;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                Secured secured = handlerMethod.getMethod().getAnnotation(Secured.class);

                if (secured == null)
                    return true;

                final String bearer = "Bearer ";
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if ((authHeader == null || authHeader.equals("")) || !authHeader.startsWith(bearer)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header token key is not set");
                    return false;
                } else {
                    String token = authHeader.substring(bearer.trim().length()).trim();
                    validateToken(token);
                    return true;
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }
        } catch (InvalidJwtException invalidJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authorization token from request");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) {
        /*do nothing*/
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) {
        /*do nothing*/
    }

    private void validateToken(String token) throws Exception {
        String jwkJson = "{\"kty\":\"oct\",\"k\":\"" + "ql7DBZomekbNg9gDs1j4-RSc2sBnoOspoHLU61pTxkY" + "\"}";
        JsonWebKey jwk = JsonWebKey.Factory.newJwk(jwkJson);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("SECURE")
                .setDecryptionKey(jwk.getKey())
                .setVerificationKey(jwk.getKey())
                .build();
        setSecurityContextHolder(jwtConsumer, token);
    }

    private void setSecurityContextHolder(JwtConsumer jwtConsumer, String token) throws InvalidJwtException, MalformedClaimException {
        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
        securityContextHolder.setAccessToken(token);
        securityContextHolder.setId(jwtClaims.getSubject());
        securityContextHolder.setCredential(jwtClaims.getClaimValue("SECURE_OBJ"));
        securityContextHolder.setExpiration(LocalDateTime.ofInstant(Instant.ofEpochMilli(jwtClaims.getIssuedAt().getValueInMillis()),
                ZoneId.systemDefault()));
        securityContextHolder.setIssuer(jwtClaims.getIssuer());
    }
}
