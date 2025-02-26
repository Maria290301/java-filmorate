package ru.yandex.practicum.User;

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
import ru.yandex.practicum.controller.UserController;
import ru.yandex.practicum.exception.UserNotFoundException;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.storage.UserStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

        when(userStorage.updateUser(any(User.class))).thenReturn(userToUpdate);
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

    @Test
    public void removeFriendValidIdsReturnsNoContent() throws Exception {
        doNothing().when(userService).removeFriend(1, 2);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).removeFriend(1, 2);
    }

    @Test
    public void removeFriendInvalidUserReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с ID 1 или друг с ID 2 не найден")).when(userService).removeFriend(anyInt(), anyInt());

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с ID 1 или друг с ID 2 не найден"));
    }

    @Test
    public void getFriendsReturnsListOfFriends() throws Exception {
        int userId = 1;

        User friend1 = new User();
        friend1.setId(2);
        friend1.setEmail("friend1@example.com");
        friend1.setLogin("friend1");
        friend1.setName("Friend1");
        friend1.setBirthday(LocalDate.of(1990, 1, 1));

        User friend2 = new User();
        friend2.setId(3);
        friend2.setEmail("friend2@example.com");
        friend2.setLogin("friend2");
        friend2.setName("Friend2");
        friend2.setBirthday(LocalDate.of(1991, 2, 2));

        List<User> friends = Arrays.asList(friend1, friend2);

        when(userStorage.getFriendsByUserId(userId)).thenReturn(friends);
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");
        user.setLogin("User ");
        user.setBirthday(LocalDate.of(1995, 1, 1));

        when(userStorage.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}/friends", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Friend1"))
                .andExpect(jsonPath("$[1].name").value("Friend2"));

        verify(userStorage).getFriendsByUserId(userId);
        verify(userStorage).getUserById(userId);
    }

    @Test
    public void getFriendsForNonExistentUserReturnsNotFound() throws Exception {
        doThrow(new UserNotFoundException("Пользователь с ID 1 не найден")).when(userService).getFriends(1);

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с ID 1 не найден"));
    }
}