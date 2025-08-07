import common.CommonIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    // 3-A
    // 1. Arrange: set up the data
    // 2. Act: code that triggers what we want to test
    // 3. Assert: assert what we want to test

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken() {
        String loginPayload = CommonIntegrationTest.loginPayload;
        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                // act
                .when().post("/auth/login")
                // assert
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated Token: " + response.jsonPath().getString("token"));
    }

    @Test
    public void shouldReturnUnauthorizedOrInvalidLogin() {
        String loginPayload = """
                {
                    "email": "invalid@test.com",
                    "password": "invalid-passsword",
                }
                """;
        given()
                .contentType("application/json")
                .body(loginPayload)
                .when().post("/auth/login")
                .then()
                .statusCode(401);
    }
}
