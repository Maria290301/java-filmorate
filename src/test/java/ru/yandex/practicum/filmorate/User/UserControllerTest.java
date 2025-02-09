package ru.yandex.practicum.filmorate.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.controller.UserController;
import ru.yandex.practicum.model.User;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void addUserValidUserReturnsCreated() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void addUserInvalidEmailReturnsBadRequest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("invalidEmail");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserEmptyLoginReturnsBadRequest() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String userJson = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUseValidUserReturnsOk() throws Exception {
        User newUser  = new User();
        newUser .setId(1);
        newUser .setEmail("original@example.com");
        newUser .setLogin("originaluser");
        newUser .setName("Original User");
        newUser .setBirthday(LocalDate.of(1990, 1, 1));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(newUser )))
                .andExpect(status().isCreated());
        User updatedUser  = new User();
        updatedUser .setEmail("updated@example.com");
        updatedUser .setLogin("updateduser");
        updatedUser .setName("Updated User");
        updatedUser .setBirthday(LocalDate.of(1990, 1, 1));
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(updatedUser )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.login").value("updateduser"))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    public void getUsers_ReturnsListOfUsers() throws Exception {
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("test1@example.com");
        user1.setLogin("login1");
        user1.setName("User  One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        User user2 = new User();
        user2.setId(2);
        user2.setEmail("test2@example.com");
        user2.setLogin("login2");
        user2.setName("User  Two");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        userController.addUser (user1);
        userController.addUser (user2);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
