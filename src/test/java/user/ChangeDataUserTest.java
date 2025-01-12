package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class ChangeDataUserTest {
    private UserClient userClient;
    private User user;
    private String bearerToken;

    @Before
    public void setUp() {
        user = getRandomUser();
        userClient = new UserClient();
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка изменения данных пользователя с авторизацией")
    public void changeDataUserWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка изменения данных пользователя без авторизации")
    public void changeDataUserWithoutAuthorization() {
        bearerToken = null;

        User secondUser = getRandomUser();

        ValidatableResponse responsePatch = userClient.patch(secondUser, bearerToken);

        responsePatch.assertThat().statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .and().body("message", is("You should be authorised"));
    }


    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}
