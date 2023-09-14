package com.example.tutorials.junit.ui.controllers;

import com.example.tutorials.junit.service.UsersService;
import com.example.tutorials.junit.shared.UserDto;
import com.example.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.example.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UsersControllerTest {

    public static final String METHOD_ARGUMENT_NOT_VALID_EXCEPTION_SHOULD_BE_THROWN = "MethodArgumentNotValidException should be thrown";

    public static final String INCORRECT_HTTP_STATUS_CODE_RETURNED = "Incorrect HTTP Status code returned";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    private final UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();

    private  UserDto userDto;

    @BeforeEach
    void setUp() {
        userDetailsRequestModel.setFirstName("John");
        userDetailsRequestModel.setLastName("Smith");
        userDetailsRequestModel.setEmail("johns@mail.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("12345678");

        userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_thenReturnCreateUserDetails() throws Exception {
        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        UserRest createdUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);

        assertEquals(userDetailsRequestModel.getFirstName(),
                createdUser.getFirstName(), "The returned user first name is incorrect");

        assertEquals(userDetailsRequestModel.getLastName(),
                createdUser.getLastName(), "The returned user last name is incorrect");

        assertFalse(createdUser.getUserId().isEmpty(), "userId should not be empty");
    }

    @ParameterizedTest
    @CsvSource({
            "'', Smith, johns@mail.com, 12345678, 12345678",
            "John, '', johns@mail.com, 12345678, 12345678",
            "John, Smith, '', 12345678, 12345678",
            "John, Smith, johns@mail.com, '', 12345678",
            "John, Smith, johns@mail.com, 12345678, ''",
    })
    @DisplayName("Missing fields throw MethodArgumentNotValidException")
    void testCreateUser_whenProvideNullFields_thenThrowMethodArgumentNotValidException(
            String firstName, String lastName, String email, String password, String repeatPassword
    ) throws Exception {
        userDetailsRequestModel.setFirstName(firstName);
        userDetailsRequestModel.setLastName(lastName);
        userDetailsRequestModel.setEmail(email);
        userDetailsRequestModel.setPassword(password);
        userDetailsRequestModel.setRepeatPassword(repeatPassword);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(MethodArgumentNotValidException.class, Objects.requireNonNull(mvcResult.getResolvedException()).getClass(),
                METHOD_ARGUMENT_NOT_VALID_EXCEPTION_SHOULD_BE_THROWN);
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(),
                INCORRECT_HTTP_STATUS_CODE_RETURNED);
    }

    @ParameterizedTest
    @CsvSource({
            "J, Smith, jones@email.com, 12345678, 12345678, First name must not be less than 2 characters",
            "John, S, jones@email.com, 12345678, 12345678, Last name must not be less than 2 characters",
            "John, Smith, jones@email.com, 1234567, 12345678, Password must be equal to or greater than 8 characters and less than 16 characters",
            "John, Smith, jones@email.com, 12345678, 1234567, Repeat Password must be equal to or greater than 8 characters and less than 16 characters",
            "John, Smith, jones@email.com, 12345678912345678, 12345678, Password must be equal to or greater than 8 characters and less than 16 characters",
            "John, Smith, jones@email.com, 12345678, 12345678912345678, Repeat Password must be equal to or greater than 8 characters and less than 16 characters",
    })
    @DisplayName("Incorrect size should throw MethodArgumentNotValidException")
    void testCreateUser_whenProvideFieldsWithIncorrectSize_thenThrowMethodArgumentNotValidException(
            String firstName, String lastName, String email, String password, String repeatPassword, String errorMessage
    ) throws Exception {
        userDetailsRequestModel.setFirstName(firstName);
        userDetailsRequestModel.setLastName(lastName);
        userDetailsRequestModel.setEmail(email);
        userDetailsRequestModel.setPassword(password);
        userDetailsRequestModel.setRepeatPassword(repeatPassword);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(MethodArgumentNotValidException.class, Objects.requireNonNull(mvcResult.getResolvedException()).getClass(),
                METHOD_ARGUMENT_NOT_VALID_EXCEPTION_SHOULD_BE_THROWN);
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(),
                INCORRECT_HTTP_STATUS_CODE_RETURNED);
        assertTrue(mvcResult.getResponse().getContentAsString().contains(errorMessage));
    }
}