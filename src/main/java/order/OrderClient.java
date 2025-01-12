package order;

import utils.Endpoints;
import utils.Specifications;
import data.Order;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient {
    @Step("Send post request to api/orders")
    public ValidatableResponse create(Order order, String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .and()
                .body(order)
                .when()
                .post(Endpoints.CREATE_ORDER_API)
                .then();
    }

    @Step("Send get request to api/ingredients/")
    public static ValidatableResponse getAllIngredients() {
        return given()
                .spec(Specifications.requestSpecification())
                .get(Endpoints.INGREDIENT_API)
                .then();
    }

    @Step("Send get request to api/orders")
    public static ValidatableResponse getClientOrder(String bearerToken) {
        return given()
                .spec(Specifications.requestSpecification())
                .headers("Authorization", bearerToken)
                .get(Endpoints.USER_ORDERS_API)
                .then();
    }
}
