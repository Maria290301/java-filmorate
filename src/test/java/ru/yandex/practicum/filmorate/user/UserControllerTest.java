package ru.yandex.practicum.filmorate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserStorage userStorage;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void addUserValidUserReturnsCreated() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("validLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        lenient().when(userStorage.addUser(any(User.class))).thenReturn(user);

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
    public void getUsersReturnsListOfUsers() throws Exception {
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
        userController.addUser(user1);
        userController.addUser(user2);
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUseValidUserReturnsOk() throws Exception {
        User userToUpdate = new User();
        userToUpdate.setId(1);
        userToUpdate.setEmail("updated@example.com");
        userToUpdate.setLogin("updatedLogin");
        userToUpdate.setName("Updated User");

        // Мокируем поведение userStorage
        when(userStorage.updateUser(any(User.class))).thenReturn(userToUpdate);

        // Преобразование объекта User в JSON
        String userJson = objectMapper.writeValueAsString(userToUpdate);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.login").value("updatedLogin"))
                .andExpect(jsonPath("$.name").value("Updated User"));
    }

    @Test
    public void addFriendValidIdsReturnsOk() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Друг успешно добавлен"));

        verify(userService, times(1)).addFriend(1, 2);
    }

    @Test
    public void addFriendInvalidUserReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException("User  not found")).when(userService).addFriend(anyInt(), anyInt());

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNotFound());
    }
}