package ru.yandex.practicum.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @NotNull
    private int id;
    @NotNull
    private int friendId;
    @NotNull
    private boolean isFriend;
}
