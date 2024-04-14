package io.github.dearrudam.backendchallenge;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtValidatorTest implements TokenSupport {

    @Test
    void should_be_valid_given_an_valid_token() {
        assertTrue(JwtValidator.isValid(createValidToken()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidTokens")
    void should_be_invalid_given(String scenario, String rawToken) {
        assertFalse(JwtValidator.isValid(rawToken));
    }

}
