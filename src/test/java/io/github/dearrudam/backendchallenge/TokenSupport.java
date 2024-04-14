package io.github.dearrudam.backendchallenge;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import net.datafaker.Faker;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public interface TokenSupport {

    String VALID_JOSE_HEADER_SAMPLE = "eyJhbGciOiJIUzI1NiJ9";
    String VALID_SIGNATURE_SAMPLE = "QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg";
    String[] ValidRole = {"Admin", "Member", "External"};
    Faker faker = new Faker();

    default String createValidToken() {
        return jwtWithClaims(builder -> builder
                .add("Name", validName())
                .add("Role", validRole())
                .add("Seed", validSeed()));
    }

    static String jwtWithClaims(Function<JsonObjectBuilder, JsonObjectBuilder> claimsBuilder) {
        return jwtWithClaims(claimsBuilder.apply(Json.createObjectBuilder()).build());
    }

    static String jwtWithClaims(JsonObject claims) {
        return jwtWithClaims(claims::toString);
    }

    static String jwtWithClaims(Supplier<String> claimsSupplier) {
        return jwtWithClaims(claimsSupplier.get());
    }

    static String jwtWithClaims(String claims) {
        if (claims == null)
            return null;
        return VALID_JOSE_HEADER_SAMPLE
                + "."
                + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(claims.getBytes(StandardCharsets.UTF_8))
                + "."
                + VALID_SIGNATURE_SAMPLE;
    }

    static Set<Arguments> jsonWithInvalidClaims() {
        return Set.of(
                arguments("a nullable json", null),
                arguments("a json with no claims",
                        Json.createObjectBuilder().build().toString()),
                arguments("a json with a Name claim containing numeric characters",
                        Json.createObjectBuilder()
                                .add("Name", validName() + faker.number().randomDigit())
                                .add("Role", validRole())
                                .add("Seed", validSeed()).build().toString()),
                arguments("a json with more than 3 claims (only accepts Name, Role and Seed)",
                        Json.createObjectBuilder()
                                .add("Name", validName())
                                .add("Role", validRole())
                                .add("Org", anyValue())
                                .add("Seed", validSeed()).build().toString()),
                arguments("a json with a Seed Claim containing not prime number",
                        Json.createObjectBuilder()
                                .add("Name", validName())
                                .add("Role", validRole())
                                .add("Seed", invalidSeedWithNonPrimeNumber()).build().toString()),
                arguments("a json with invalid Role Claim (only Admin, Member and External is accepted)",
                        Json.createObjectBuilder()
                                .add("Name", validName())
                                .add("Role", invalidRole())
                                .add("Seed", validSeed()).build().toString()),
                arguments("a json with invalid Name Claim (max 256 characters are accepted)",
                        Json.createObjectBuilder()
                                .add("Name", invalidNameWithExceedLength())
                                .add("Role", validRole())
                                .add("Seed", validSeed()).build().toString())
        );
    }

    static Set<Arguments> invalidTokens() {
        Set<Arguments> scenarios = new LinkedHashSet<>();
        scenarios.addAll(Set.of(
                arguments("an empty token", ""),
                arguments("a blank token", " "),
                arguments("a token with invalid claims",
                        jwtWithClaims("\"u�\\u001d~��Ȕ��\\u001aȎ��Y\\u001bZ[��\\b�ٮB#�#s�C\\u0012\\\"�$�\\u0016�R#�%F������\\u0004\\u0017&\\u0017V��'\""))
        ));
        scenarios.addAll(jsonWithInvalidClaims()
                .stream()
                .map(Arguments::get)
                .map(args -> arguments(((String) args[0]).replaceAll("json", "token"), jwtWithClaims((String) args[1])))
                .collect(Collectors.toSet()));
        return scenarios;
    }

    private static String anyValue() {
        return UUID.randomUUID().toString();
    }

    private static int invalidSeedWithNonPrimeNumber() {
        return 6;
    }

    private static String invalidNameWithExceedLength() {
        return "X".repeat(257);
    }

    private static String invalidRole() {
        return UUID.randomUUID().toString();
    }

    static int validSeed() {
        return 3;
    }

    static String validName() {
        return faker.name().fullName();
    }

    static String validRole() {
        return ValidRole[faker.number().numberBetween(0, 2)];
    }
}
