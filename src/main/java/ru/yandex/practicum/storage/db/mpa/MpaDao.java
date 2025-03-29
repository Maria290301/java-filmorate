package ru.yandex.practicum.storage.db.mpa;

import ru.yandex.practicum.model.Mpa;

import java.util.List;

public interface MpaDao {

    Mpa getMpaById(Integer id);

    List<Mpa> getMpaList();

    boolean isContains(Integer id);
}
