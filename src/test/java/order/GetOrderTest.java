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

import static order.OrderGenerator.getListOrder;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class GetOrderTest {
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
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка получения заказов авторизованного пользователя")
    public void createOrderWithAuthorizationTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        userClient.login(user);
        orderClient.create(order, bearerToken);

        ValidatableResponse responseOrderUser = OrderClient.getClientOrder(bearerToken);

        responseOrderUser.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "Order's test")
    @DisplayName("Получение заказов не авторизованного пользователя")
    @Description("Проверка получения заказов не авторизованного пользователя")
    public void createOrderWithoutAuthorizationTest() {
        bearerToken = "";
        ValidatableResponse getClientOrder = OrderClient.getClientOrder(bearerToken);

        getClientOrder.assertThat().statusCode(SC_UNAUTHORIZED).body("success", is(false)).and()
                .body("message", is("You should be authorised"));
    }


    @After
    public void tearDown() {

        if (bearerToken.equals("")) return;
        userClient.delete(bearerToken);

    }
}
