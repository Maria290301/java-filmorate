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
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class UserControllerTest {

    private MockMvc mockMvc;

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

        lenient().when(userService.addUser(any(User.class))).thenReturn(user);

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

        when(userService.updateUser(any(User.class))).thenReturn(userToUpdate);
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
        doNothing().when(userService).addFriend(1, 2);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).addFriend(1, 2);
    }

    @Test
    public void addFriendInvalidUserReturnsNotFound() throws Exception {
        int userId = 1;
        int friendId = 2;

        doThrow(new UserNotFoundException("User not found")).when(userService).addFriend(userId, friendId);

        mockMvc.perform(put("/{userId}/friends/{friendId}", userId, friendId))
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
        int userId = 1;
        int friendId = 2;

        doThrow(new UserNotFoundException("User not found")).when(userService).removeFriend(userId, friendId);

        mockMvc.perform(delete("/{userId}/friends/{friendId}", userId, friendId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFriendsReturnsListOfFriends() throws Exception {
        User friend1 = mock(User.class);
        when(friend1.getId()).thenReturn(2);
        when(friend1.getName()).thenReturn("Friend1");

        User friend2 = mock(User.class);
        when(friend2.getId()).thenReturn(3);
        when(friend2.getName()).thenReturn("Friend2");

        List<User> friends = Arrays.asList(friend1, friend2);

        when(userService.getFriends(1)).thenReturn(friends);

        mockMvc.perform(get("/users/1/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(3));
    }

    @Test
    public void getFriendsReturnsEmptyListWhenUserNotFound() throws Exception {
        when(userService.getFriends(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/999/friends")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}