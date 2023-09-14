package com.example.tutorials.junit.ui.controllers;

import com.example.tutorials.junit.security.SecurityConstants;
import com.example.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersControllerIntegrationTest {

    @LocalServerPort
    private int serverPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String token;

    private String userID;

    @Test
    @DisplayName("User can be created")
    @Order(1)
    void testCreateUser_whenValidDetailsProvided_thenReturnUserDetails() throws JSONException {
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstName", "John");
        userDetailsRequestJson.put("lastName", "Smith");
        userDetailsRequestJson.put("email", "jones@email.com");
        userDetailsRequestJson.put("password", "12345678");
        userDetailsRequestJson.put("repeatPassword", "12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> httpEntity = new HttpEntity<>(userDetailsRequestJson.toString(), headers);

        ResponseEntity<UserRest> userRestResponseEntity = testRestTemplate.postForEntity("/users", httpEntity, UserRest.class);

        UserRest createdUser = userRestResponseEntity.getBody();

        assertEquals(userDetailsRequestJson.get("firstName"), Objects.requireNonNull(createdUser).getFirstName(), "The first name is incorrect");
        assertEquals(userDetailsRequestJson.get("lastName"), createdUser.getLastName(), "The last name is incorrect");
        assertEquals(userDetailsRequestJson.get("email"), createdUser.getEmail(), "The email is incorrect");
        assertEquals(HttpStatus.OK, userRestResponseEntity.getStatusCode(), "Status is incorrect");
    }

    @Test
    @DisplayName("GET /users requires JWT")
    @Order(2)
    void testGetUsers_whenMissingJWT_thenReturn403() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Http status code should be 403 Forbidden");
    }

    @Test
    @DisplayName("/login works")
    @Order(3)
    void testUserLogin_whenValidCredentialsProvided_thenReturnsJwtInAuthorizationHeader() throws JSONException {
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "jones@email.com");
        loginCredentials.put("password", "12345678");

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString());

        ResponseEntity<Object> response = testRestTemplate.postForEntity("/users/login", request, null);

        token = response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);
        userID = response.getHeaders().getValuesAsList("UserID").get(0);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected status code should be 200 OK");
        assertNotNull(token,
                "Response should contain Authorization header with JWT");
        assertNotNull(userID,
                "Response should contain UserID header");
    }

    @Test
    @DisplayName("GET /users works")
    @Order(4)
    void testGetUsers_whenValidJwtProvided_thenReturnsUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        HttpEntity httpEntity = new HttpEntity(headers);

        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200 OK");
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size(), "There should be exactly 1 user in the list");
    }
}
