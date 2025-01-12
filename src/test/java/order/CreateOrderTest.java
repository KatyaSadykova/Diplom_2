package order;

import data.Order;
import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import java.util.ArrayList;
import java.util.List;

import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateOrderTest {
    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private Order order;

    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
        order = getListOrder();
        orderClient = new OrderClient();
    }

    @Test
    @Epic(value = "Order's test")
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией")
    public void createOrderWithAuthorizationTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Order's test")
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Order's test")
    @DisplayName("Создание заказа без ингридиентов")
    @Description("Проверка создания заказа без ингридиентов")
    public void createOrderWithoutIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");

        order.setIngredients(java.util.Collections.emptyList());

        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST).body("success", is(false)).and().body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @Epic(value = "Order's test")
    @DisplayName("Создание заказа с неправильными ингридиентами")
    @Description("Проверка создания заказа с неправильными ингридиентами")
    public void createOrderWithWrongIngridientTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");

        List wrongIngridient = new ArrayList();
        wrongIngridient.add("60d3b41abdacab0026a733c6");

        order.setIngredients(wrongIngridient);

        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        responseCreateOrder.assertThat().statusCode(SC_BAD_REQUEST).body("success", is(false)).and().body("message", is("One or more ids provided are incorrect"));
    }


    @After
    public void tearDown() {

        if (bearerToken.equals("")) return;
        userClient.delete(bearerToken);

    }
}
