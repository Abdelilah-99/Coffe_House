package com.blog.repository;

import com.blog.entity.User;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    Optional<User> findByUuid(String uuid);

    List<User> findByUuidNotAndUserNameStartingWith(String crrUser, String prefix);
}
