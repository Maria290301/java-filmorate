package ru.yandex.practicum.storage.db.genre;

import ru.yandex.practicum.model.Genre;
import java.util.List;

public interface GenreDao {

    Genre getGenreById(Integer id);

    List<Genre> getGenres();

    boolean isContains(Integer id);
}
