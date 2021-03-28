package org.itstep.domain.dao.Impl;

import org.itstep.App1;
import org.itstep.domain.dao.UserDao;
import org.itstep.domain.entity.User;
import org.itstep.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

public class UserDaoImpl implements UserDao {

    public final ConfigurableApplicationContext context = SpringApplication.run(App1.class);
    private UserRepository userRepository = context.getBean(UserRepository.class);

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
        if (isExist(id)) return findById(id).isUser();
        return false;
    }

    @Override
    public boolean isAdmin(Integer id) {
        if (isExist(id)) return findUserById(id).isAdmin() || isMaster(id);
        return false;
    }

    @Override
    public boolean isMaster(Integer id) {
        return findUserById(id).isMaster();
    }

    @Override
    public User setAsUser(Integer id) {
        User user = findUserById(id);
        user.setUser(true);
        return userRepository.saveAndFlush(user);
    }
}
