package org.itstep.domain.dao.Impl;

import org.itstep.domain.dao.UserDao;
import org.itstep.domain.entity.User;
import org.itstep.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;
    @Autowired
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User entity) {
        userRepository.save(entity);
    }

    @Override
    public User findById(Integer integer) {
        return userRepository.findUserByUserId(integer);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User entity) {
        return userRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(User entity) {
        userRepository.delete(userRepository.findUserByUserId(entity.getUserId()));
    }

    @Override
    public User findUserById(Integer id) {
        return userRepository.findUserByUserId(id);
    }

    @Override
    public boolean isExist(Integer id) {
        return (findUserById(id) != null);
    }

    @Override
    public boolean isUser(Integer id) {
        if (isExist(id)) {
            User user = findById(id);
            return (user.isUser() & user.isAdmin() & user.isMaster());
        }
        return false;
    }

    @Override
    public boolean isAdmin(Integer id) {
        if (isExist(id)) {
            User user = findUserById(id);
            return user.isAdmin() | user.isMaster();
        }
        return false;
    }

    @Override
    public boolean isMaster(Integer id) {
        if (isExist(id)) {
            return findUserById(id).isMaster();
        }
        return false;
    }

    @Override
    public boolean isRegistered(Integer id) {
        if (isExist(id)) {
            User user = findById(id);
            return (user.isUser() | user.isAdmin() | user.isMaster());
        }
        return false;
    }

    @Override
    public User setAsUser(Integer id) {
        User user = findUserById(id);
        user.setUser(true);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public List<User> findAllNotAuthorized() {
        return userRepository.findAllByIsUserIsFalseAndIsAdminIsFalseAndIsMasterIsFalse();
    }

}
