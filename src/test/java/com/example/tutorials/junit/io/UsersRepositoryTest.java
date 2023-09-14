package com.example.tutorials.junit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UsersRepositoryTest {
    
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UsersRepository usersRepository;

    private UserEntity userEntity1 = new UserEntity();

    private UserEntity userEntity2 = new UserEntity();

    @BeforeEach
    void setUp() {
        userEntity1.setUserId(UUID.randomUUID().toString());
        userEntity1.setFirstName("john");
        userEntity1.setLastName("smith");
        userEntity1.setEmail("jones@mail.net");
        userEntity1.setEncryptedPassword("12345678");

        testEntityManager.persistAndFlush(userEntity1);

        userEntity2.setUserId(UUID.randomUUID().toString());
        userEntity2.setFirstName("maria");
        userEntity2.setLastName("jones");
        userEntity2.setEmail("mariaj@test.com");
        userEntity2.setEncryptedPassword("87654321");

        testEntityManager.persistAndFlush(userEntity2);
    }

    @Test
    void testFindByEmail_whenGivenCorrectEmail_thenReturnUserEntity() {
        UserEntity storedUser = usersRepository.findByEmail(userEntity1.getEmail());

        assertEquals(userEntity1.getEmail(), storedUser.getEmail(), "Returned email does not correspond with the provided one");
    }

    @Test
    void testFindByUserId_whenGivenCorrectUserId_thenReturnUserEntity() {
        UserEntity storedUser = usersRepository.findByUserId(userEntity1.getUserId());
        
        assertEquals(userEntity1.getUserId(), storedUser.getUserId(), "Returned user id does not correspond with the provided one");
    }

    @Test
    void testFindByEmailEndWith_whenGivenCorrectEmailEnd_thenReturnUserEntity() {
        UserEntity storedUser = usersRepository.findByEmailEndsWith(".net");

        assertEquals(userEntity1.getEmail(), storedUser.getEmail(), "Returned email does not correspond with the provided one");
    }

    @Test
    void testFindUsersWithEmailEndsWith_whenGiveEmailDomain_thenReturnUsersWithGivenDomain() {
        String emailDomain = "@test.com";

        List<UserEntity> storedUsers = usersRepository.findUsersWithEmailEndingWith(emailDomain);

        assertEquals(1, storedUsers.size(), "There should be only one user in the list");
        assertTrue(storedUsers.stream()
                .allMatch(userEntity -> userEntity.getEmail().endsWith(emailDomain)),
                "Email does not ends with the given email domain name");
    }
}