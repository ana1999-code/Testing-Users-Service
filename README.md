# UserService Testing Project

Welcome to the UserService Testing Project repository! This project is focused on testing various layers of a UserService application, including the database layer, controller layer, and more. Below, you'll find information about the testing approaches, what has been tested, and the tools and libraries used.

## Table of Contents
- [Testing Approaches](#testing-approaches)
- [Test Classes](#test-classes)
  - [UsersRepositoryTest](#usersrepositorytest)
  - [UsersControllerIntegrationTest](#userscontrollerintegrationtest)
  - [UsersControllerTest](#userscontrollertest)
- [Tools and Libraries](#tools-and-libraries)
- [How to Run Tests](#how-to-run-tests)

## Testing Approaches

### 1. DataJpaTest for Database Layer Testing
- In the `UsersRepositoryTest` class, we use `@DataJpaTest` to create a Spring Boot test slice for testing the database layer.
- We utilize `TestEntityManager` to interact with the in-memory database.
- We test various database operations such as creating and querying user entities.
- Sample tests include finding users by email, user ID, and more.

### 2. SpringBootTest for Controller Layer Integration Testing
- In the `UsersControllerIntegrationTest` class, we use `@SpringBootTest` with a random port to perform integration testing of the controller layer.
- We utilize `TestRestTemplate` to send HTTP requests and validate responses.
- We test controller endpoints, user creation, authentication, and user retrieval.
- JWT authentication and authorization are also tested in this class.

### 3. UsersControllerTest for Controller Layer Unit Testing
- In the `UsersControllerTest` class, we perform unit testing of the controller layer.
- We use `@WebMvcTest` to create a test slice for the controller.
- Tests include creating users, handling missing fields, and validating field sizes.

## Test Classes

### UsersRepositoryTest
- Tests the database layer using `@DataJpaTest`.
- Verifies database operations such as finding users by email, user ID, and more.

### UsersControllerIntegrationTest
- Performs integration testing of the controller layer using `@SpringBootTest` with a random port.
- Tests various controller endpoints, user creation, authentication, and user retrieval.
- Includes tests for JWT authentication and authorization.

### UsersControllerTest
- Performs unit testing of the controller layer using `@WebMvcTest`.
- Tests user creation, handling missing fields, and validating field sizes.

## Tools and Libraries

- **Spring Boot Test**: Used for creating slices and integration tests.
- **TestEntityManager**: Used for database layer testing with an in-memory database.
- **TestRestTemplate**: Used for integration testing of controller endpoints.
- **JUnit 5**: Framework for writing unit and integration tests.
- **Mockito**: Used for mocking and stubbing dependencies in unit tests.
- **ObjectMapper**: Used for JSON serialization and deserialization.

## How to Run Tests

1. Ensure you have the required dependencies and a running Spring Boot application.
2. Run the tests using your preferred IDE or build tool (e.g., Maven or Gradle).
3. Monitor test results for pass or fail status.

Feel free to explore the test classes and expand upon them to cover additional scenarios and functionality.

Happy testing!
