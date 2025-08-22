package com.lhh.techjobs.service;

import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.signerKey}")
    private String SINGER_KEY;

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("lehuuhau.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("role", user.getRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't not generate token: ", e);
            throw new RuntimeException(e);
        }
    }

    public boolean validateToken(String token) {
        try {
            JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean verified = signedJWT.verify(verifier);
            JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();

            return verified && claimSet.getExpirationTime().after(new Date());
        } catch (Exception e) {
            log.error("Error validating token: ", e);
            return false;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
            return claimSet.getClaim("role").toString();
        } catch (Exception e) {
            log.error("Error getting role from token: ", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimSet = signedJWT.getJWTClaimsSet();
            return claimSet.getSubject();
        } catch (Exception e) {
            log.error("Error getting username from token: ", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    public JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            log.error("Error getting claims from token: ", e);
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }
}
