package org.itstep.domain.dao;

import org.itstep.domain.entity.User;

import javax.persistence.criteria.CriteriaBuilder;

public interface UserDao extends Dao<User, Integer> {
    User findUserById(Integer id);
    boolean isExist(Integer id);
    boolean isUser(Integer id);
    boolean isAdmin(Integer id);
    boolean isMaster(Integer id);
    User setAsUser(Integer id);
}
