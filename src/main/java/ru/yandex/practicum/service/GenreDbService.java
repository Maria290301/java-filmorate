package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Genre;
import ru.yandex.practicum.storage.db.genre.GenreDao;
import java.util.Collection;


@Service
@RequiredArgsConstructor
public class GenreDbService {
    private final GenreDao genreDao;

    public Genre getGenreById(Integer id) {
        if (id == null || !genreDao.isContains(id)) {
            throw new NotFoundException("Negative or empty id was passed");
        }
        return genreDao.getGenreById(id);
    }

    public Collection<Genre> getGenres() {
        return genreDao.getGenres();
    }
}
