package com.example.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserEntityTest {

    @Autowired
    private TestEntityManager testEntityManager;

    private  UserEntity userEntity = new UserEntity();


    @BeforeEach
    void setUp() {
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName("john");
        userEntity.setLastName("smith");
        userEntity.setEmail("jones@test.com");
        userEntity.setEncryptedPassword("12345678");
    }

    @Test
    void testUserEntity_whenValidUserDetailsProvided_thenReturnStoredUserDetails() {
        UserEntity storedUserEntity = testEntityManager.persistAndFlush(userEntity);

        assertTrue(storedUserEntity.getId() > 0);
        assertEquals(userEntity.getUserId(), storedUserEntity.getUserId(), "The stored id does not correspond with the provided one");
        assertEquals(userEntity.getFirstName(), storedUserEntity.getFirstName(), "The stored first name does not correspond with the provided one");
        assertEquals(userEntity.getLastName(), storedUserEntity.getLastName(), "The stored last name does not correspond with the provided one");
        assertEquals(userEntity.getEmail(), storedUserEntity.getEmail(), "The stored email does not correspond with the provided one");
        assertEquals(userEntity.getEncryptedPassword(), storedUserEntity.getEncryptedPassword(), "The stored password does not correspond with the provided one");
    }

    @Test
    void testUserEntity_whenFirstNameIsTooLong_thenThrowException() {
        userEntity.setFirstName("johnjohnjohnjohnjohnjohnjohnjohnjohnjohnjohnjohnjohn");

        assertThrows(PersistenceException.class,
                () -> testEntityManager.persistAndFlush(userEntity),
                "Was expecting PersistenceException to be thrown");
    }

    @Test
    void testUserEntity_whenSaveTwoUsersWithTheSameUserId_thenThrowException() {
        testEntityManager.persistAndFlush(userEntity);

        UserEntity user = new UserEntity();
        user.setUserId(userEntity.getUserId());
        user.setFirstName("maria");
        user.setLastName("jonson");
        user.setEmail("mariajtest@email.com");
        user.setEncryptedPassword("pass1234");

        assertThrows(PersistenceException.class,
                () -> testEntityManager.persistAndFlush(user),
                "Was expecting PersistenceException to be thrown");
    }
}