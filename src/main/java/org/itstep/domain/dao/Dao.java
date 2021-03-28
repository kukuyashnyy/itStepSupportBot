package org.itstep.domain.dao;

import java.util.List;

public interface Dao<T, ID> {
    void save(T entity);
    T findById(ID id);
    List<T> findAll();
    T update(T entity);
    void delete(T entity);
}
