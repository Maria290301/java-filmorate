package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.Mpa;
import ru.yandex.practicum.storage.db.mpa.MpaDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public Mpa getMpaById(Integer id) {
        if (id == null || !mpaDao.isContains(id)) {
            throw new NotFoundException("Negative or empty id was passed");
        }
        return mpaDao.getMpaById(id);
    }

    public Collection<Mpa> getMpaList() {
        return mpaDao.getMpaList();
    }
}
