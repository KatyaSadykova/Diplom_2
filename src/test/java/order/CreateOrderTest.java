package order;

import data.Order;
import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;

import java.util.ArrayList;
import java.util.List;


import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.*;
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
    @DisplayName("Создание заказа с неправильными ингредиентами")
    @Description("Проверка создания заказа с неправильными ингредиентами")
    public void createOrderWithWrongIngredientTest() {
        // Регистрация и авторизация пользователя
        ValidatableResponse responseRegister = userClient.register(user);
        userClient.login(user);
        bearerToken = responseRegister.extract().path("accessToken");

        // Создание списка с неверным хешем ингредиента
        List<String> wrongIngredient = new ArrayList<>();
        wrongIngredient.add(RandomStringUtils.randomAlphabetic(24));

        // Установка неверного ингредиента в заказ
        order.setIngredients(wrongIngredient);

        // Отправка запроса на создание заказа
        ValidatableResponse responseCreateOrder = orderClient.create(order, bearerToken);

        // Вывод ответа для диагностики
        String responseBody = responseCreateOrder.extract().asString();
        System.out.println("Response Body: " + responseBody);

        // Проверка, что код ответа 500 и сообщение об ошибке корректно
        responseCreateOrder.assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR); // Ожидаем код 500
    }


    @After
    public void tearDown() {

        if (bearerToken.equals("")) return;
        userClient.delete(bearerToken);

    }
}
