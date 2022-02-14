package com.kitalulus.challenge.util;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

public class TokenHelper {

    private TokenHelper() {
        throw new IllegalStateException("Error TokenHelper class");
    }

    public static String generateToken(String nik, long timeout) throws Exception {
        String jwkJson = "{\"kty\":\"oct\",\"k\":\"" + "ql7DBZomekbNg9gDs1j4-RSc2sBnoOspoHLU61pTxkY" + "\"}";
        JsonWebKey jwk = JsonWebKey.Factory.newJwk(jwkJson);
        JwtClaims claims = setClaims(nik, timeout);
        JsonWebSignature jws = setJws(claims, jwk);
        JsonWebEncryption jwe = setJwe(jwk, jws);
        return jwe.getCompactSerialization();
    }

    private static JwtClaims setClaims(String value, long timeout) {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("SECURE");
        claims.setExpirationTimeMinutesInTheFuture(timeout);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject(value);
        claims.setClaim("SECURE_OBJ", value);
        return claims;
    }

    private static JsonWebSignature setJws(JwtClaims claims, JsonWebKey jwk) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKeyIdHeaderValue(jwk.getKeyId());
        jws.setKey(jwk.getKey());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        return jws;
    }

    private static JsonWebEncryption setJwe(JsonWebKey jwk, JsonWebSignature jws) throws Exception{
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.DIRECT);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(jwk.getKey());
        jwe.setKeyIdHeaderValue(jwk.getKeyId());
        jwe.setContentTypeHeaderValue("JWT");
        jwe.setPayload(jws.getCompactSerialization());
        return jwe;
    }

}
