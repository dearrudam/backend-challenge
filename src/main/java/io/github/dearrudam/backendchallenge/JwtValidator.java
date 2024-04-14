package io.github.dearrudam.backendchallenge;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Predicate;

public interface JwtValidator {

    static final Logger LOGGER = LoggerFactory.getLogger(JwtValidator.class);

    static final Predicate<JsonObject> JWT_VALIDATION_RULES =
            ((Predicate<JsonObject>) Objects::nonNull)
                    .and(Claim::containsOnlySupportedClaims)
                    .and(Claim.NAME::isValid)
                    .and(Claim.ROLE::isValid)
                    .and(Claim.SEED::isValid);


    private static JsonObject extractClaims(String token) {
        var emptyClaims = Json.createObjectBuilder().build();
        if (token == null)
            return emptyClaims;
        var jwtSections = token.split("\\.");
        if (jwtSections.length != 3)
            return emptyClaims;
        var rawClaims = jwtSections[1];
        byte[] decodedClaims;
        try {
            decodedClaims = Base64.getUrlDecoder().decode(rawClaims.getBytes());
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("cannot decode the token. {}", ex.getMessage(), ex);
            return emptyClaims;
        }
        try (var reader = Json.createReader(new InputStreamReader(new ByteArrayInputStream(decodedClaims)))) {
            return reader.readObject();
        } catch (Exception ex) {
            LOGGER.warn("cannot read the claims of the token. {}", ex.getMessage(), ex);
            return emptyClaims;
        }
    }

    static boolean isValid(String token) {
        return JWT_VALIDATION_RULES.test(extractClaims(token));
    }

}
