package com.blog.repository;

import com.blog.entity.User;

import java.util.Optional;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    Optional<User> findByUuid(String uuid);

    List<User> findByUuidNotAndUserNameStartingWith(String crrUser, String prefix);

    @Transactional
    void deleteByUuid(String uuid);

    @Query("""
            SELECT u FROM User u
            WHERE (:lastId IS NULL OR u.id < :lastId)
            ORDER BY u.id DESC
        """)
    List<User> findAllPaginated(
        @Param("lastId") Long lastId,
        Pageable pageable);
}
