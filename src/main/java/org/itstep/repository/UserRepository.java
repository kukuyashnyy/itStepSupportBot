package org.itstep.repository;

import org.itstep.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUserId(Integer userId);
    List<User> findAllByIsUserIsFalseAndIsAdminIsFalseAndIsMasterIsFalse();
}
