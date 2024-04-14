package io.github.dearrudam.backendchallenge;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the Defined JWT Claims supported
 */
public enum Claim {

    NAME {
        @Override
        protected boolean isValidClaim(JsonObject claims) {
            var value = claims.getJsonString(label()).getString();
            if (value.length() > 256)
                return false;
            return value.chars().noneMatch(Character::isDigit);
        }

    },

    ROLE {

        /**
         *  Represents the supported roles
         */
        public enum Roles {
            ADMIN, MEMBER, EXTERNAL;
        }

        @Override
        protected boolean isValidClaim(JsonObject claims) {
            var value = claims.getJsonString(label()).getString();
            return Arrays.stream(Roles.values())
                    .map(Roles::name)
                    .map(Claim::capitalize)
                    .anyMatch(value::equals);
        }
    },

    SEED {
        @Override
        protected boolean isValidClaim(JsonObject claims) {
            JsonValue jsonValue = claims.get(label());
            var value = switch (jsonValue.getValueType()) {
                case NUMBER -> claims.getInt(label());
                case STRING -> Integer.parseInt(claims.getString(label()));
                default -> 0;
            };
            return isPrime(value);
        }

        private boolean isPrime(int num) {
            if (num < 2) return false;
            for (int i = 2; i <= Math.sqrt(num); i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }

    };

    private final String label;

    Claim() {
        this.label = capitalize(name());
    }

    protected String label() {
        return this.label;
    }

    protected boolean isValid(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty())
            return false;
        if (!jsonObject.containsKey(this.label()))
            return false;
        return isValidClaim(jsonObject);
    }

    protected abstract boolean isValidClaim(JsonObject claims);

    protected static boolean containsOnlySupportedClaims(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isEmpty())
            return false;
        var claimList = Arrays.stream(Claim.values()).map(c -> c.label).toList();
        var actualClaims = new ArrayList<>(jsonObject.keySet());
        if (actualClaims.size() != claimList.size())
            return false;
        return actualClaims.containsAll(claimList);
    }

    private static String capitalize(String str) {
        return Optional.ofNullable(str)
                .map(s -> Character.toUpperCase(str.charAt(0)) + (str.substring(1)).toLowerCase())
                .orElse(str);
    }
}