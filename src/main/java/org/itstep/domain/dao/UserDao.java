package org.itstep.domain.dao;

import org.itstep.domain.entity.User;

public interface UserDao extends Dao<User, Integer> {
    User findUserByUserId(Integer id);
}
