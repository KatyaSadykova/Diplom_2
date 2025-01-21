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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
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
    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Проверка изменения email пользователя с авторизацией")
    public void changeEmailWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");

        User updatedUser = new User("newemail.com", user.getPassword(), user.getName());
        ValidatableResponse responsePatch = userClient.patch(updatedUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo("newemail.com"));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Изменение password пользователя с авторизацией")
    @Description("Проверка изменения password пользователя с авторизацией")
    public void changePasswordWithAuthorization() {

        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");


        User updatedUser = new User(user.getEmail(), "newPassword123", user.getName());


        ValidatableResponse responsePatch = userClient.patch(updatedUser, bearerToken);


        responsePatch.assertThat().statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalToIgnoringCase(user.getEmail()))
                .body("user.name", equalTo(user.getName()));

        ValidatableResponse responseLogin = userClient.login(updatedUser);
        responseLogin.assertThat().statusCode(SC_OK)
                .body("accessToken", notNullValue());
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Изменение name пользователя с авторизацией")
    @Description("Проверка изменения name пользователя с авторизацией")
    public void changeNameWithAuthorization() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");

        User updatedUser = new User(user.getEmail(), user.getPassword(), "New Name");
        ValidatableResponse responsePatch = userClient.patch(updatedUser, bearerToken);
        responsePatch.assertThat().statusCode(SC_OK)
                .body("success", is(true))
                .body("user.name", equalTo("New Name"));
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