package io.github.dearrudam.backendchallenge;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class JwtValidatorResourceTest implements TokenSupport {

    @Nested
    @DisplayName("testing " + V1Testing.URL + " endpoint")
    class V1Testing implements TokenSupport {

        public static final String URL = "v1/jwt/validate";

        @ParameterizedTest(name = "{0}")
        @DisplayName("should return http status 400 for any HTTP POST request to " + URL + " with")
        @MethodSource("invalidTokens")
        void shouldReturnHttp400(String responseBody, String token) {
            if (token == null)
                return; // cannot submit a HTTP POST request without body;
            given()
                    .when()
                    .contentType(ContentType.TEXT)
                    .accept(ContentType.TEXT)
                    .body(token)
                    .post(URL)
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.TEXT)
                    .body(is("falso"));
        }

        @Test
        @DisplayName("should return http status 200 for any HTTP POST request to " + URL + " with valid tokens")
        void shouldReturnHttp200() {
            given()
                    .when()
                    .contentType(ContentType.TEXT)
                    .accept(ContentType.TEXT)
                    .body(createValidToken())
                    .post(URL)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.TEXT)
                    .body(is("verdadeiro"));
        }

    }

    @Nested
    @DisplayName("testing " + V2Testing.URL + " endpoint")
    class V2Testing implements TokenSupport {

        public static final String URL = "v2/jwt/validate";

        @ParameterizedTest(name = "{0}")
        @DisplayName("should return http status 400 for any HTTP POST request to " + URL + " with")
        @MethodSource("invalidTokens")
        void shouldV2Return400(String responseBody, String token) {
            if (token == null)
                return; // cannot submit a HTTP POST request without body;
            given()
                    .when()
                    .contentType(ContentType.TEXT)
                    .accept(ContentType.TEXT)
                    .body(token)
                    .post(URL)
                    .then()
                    .statusCode(400)
                    .contentType(ContentType.TEXT)
                    .body(is("falso"));
        }

        @Test
        @DisplayName("should return http status 200 for any HTTP POST request to " + URL + " with valid tokens")
        void shouldReturnHttp200() {
             given()
                    .when()
                    .contentType(ContentType.TEXT)
                    .accept(ContentType.TEXT)
                    .body(createValidToken())
                    .post(URL)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.TEXT)
                    .body(is("verdadeiro"));
        }
    }
}