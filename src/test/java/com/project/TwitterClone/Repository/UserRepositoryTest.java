package com.project.TwitterClone.Repository;

import com.project.TwitterClone.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
       userRepository.save(User.builder()
                .email("alice@example.com")
                .fullName("Alice Wonderland")
                .password("Password@123")
                .createdAt("April 19, 2001")
                .build());

        userRepository.save(User.builder()
                .email("bob@example.com")
                .fullName("Bob Builder")
                .password("Password@123")
                .createdAt("April 19, 2001")
                .build());

        userRepository.save(User.builder()
                .email("carol@example.com")
                .fullName("Carol Danvers")
                .password("Password@123")
                .createdAt("April 19, 2001")
                .build());
    }
    @Test
    @Disabled
    void findByEmail() {
        User foundUser = userRepository.findByEmail("test@example.com");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(foundUser.getFullName()).isEqualTo(testUser.getFullName());
    }

    @Test
    @Disabled
    void findByEmail_ShouldReturnNull_WhenEmailDoesNotExist() {
        User foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertThat(foundUser).isNull();
    }

    @Test
    void searchUser() {
        List<User>result = userRepository.searchUser("Bob");
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Bob Builder");
    }

    @Test
    void searchUser_shouldBeCaseInsensitiveAndPartialMatch() {
        List<User> result = userRepository.searchUser("a");

        assertThat(result).isNotEmpty();
        List<String> names = result.stream().map(User::getFullName).toList();

        assertThat(names).contains("Alice Wonderland", "Carol Danvers");
    }

    @Test
    void searchUser_shouldReturnEmptyListWhenNoMatch() {
        List<User> result = userRepository.searchUser("Zelda");

        assertThat(result).isEmpty();
    }

//    @AfterEach
//    void afterEach() {
//        userRepository.delete(testUser1);
//        userRepository.delete(testUser2);
//        userRepository.delete(testUser3);
//    }
}