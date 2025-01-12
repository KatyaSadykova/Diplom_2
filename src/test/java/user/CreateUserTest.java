package user;

import data.User;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static user.UserGenerator.getRandomUser;

public class CreateUserTest {
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
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создание уникального пользователя")
    public void createUserTest() {
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_OK).body("success", is(true));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Создание не уникального пользователя")
    @Description("Проверка создания  не уникального пользователя")
    public void createAlreadyExistsUserTest() {
        ValidatableResponse responseRegisterFirstUser = userClient.register(user);
        bearerToken = responseRegisterFirstUser.extract().path("accessToken");

        ValidatableResponse responseRegisterSecondUser = userClient.register(user);
        responseRegisterSecondUser.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("User already exists"));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка пользователя без имени")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Создание пользователя без email")
    @Description("Проверка пользователя без email")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @Test
    @Epic(value = "User's test")
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse responseRegister = userClient.register(user);
        bearerToken = responseRegister.extract().path("accessToken");
        responseRegister.assertThat().statusCode(SC_FORBIDDEN).body("success", is(false)).body("message", is("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {

        if (bearerToken == null) return;
        userClient.delete(bearerToken);

    }
}
