package com.blog.repository;

import com.blog.entity.Post;
import com.blog.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);

    Optional<Post> findByUuid(String uuid);

    @Transactional
    void deleteByUuid(String uuid);

    @Query("""
                SELECT p FROM Post p where p.status != 'HIDE' 
                        and (p.createdAt < :lastTime or (p.createdAt = :lastTime and p.id < :lastId)) 
                        and (p.user.id = :userId) 
                        order by p.createdAt DESC, p.id DESC
            """)
    List<Post> findMyPostByPagination(
            @Param("lastTime") Long lastTime,
            @Param("lastId") Long lastId,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                where p.status != 'HIDE' and (p.user.id IN (
                SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) or p.user.id = :userId) and (p.createdAt < :lastTime OR (p.createdAt = :lastTime AND p.id < :lastId))
                order by p.createdAt DESC, p.id DESC
            """)
    List<Post> findByPagination(
            @Param("lastTime") Long lastTime,
            @Param("lastId") Long lastId,
            @Param("userId") Long userId,
            Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                WHERE p.user.id = :userId
                AND (p.createdAt < :lastTime
                OR (p.createdAt = :lastTime AND p.id < :lastId))
                ORDER BY p.createdAt DESC
            """)
    List<Post> findByUserPaginated(
            @Param("userId") Long userId,
            @Param("lastTime") Long lastTime,
            @Param("lastId") Long lastId,
            Pageable pageable);

    @Query("""
                SELECT p FROM Post p
                WHERE (p.createdAt < :lastTime
                OR (p.createdAt = :lastTime AND p.id < :lastId))
                ORDER BY p.createdAt DESC
            """)
    List<Post> findAllPaginated(
            @Param("lastTime") Long lastTime,
            @Param("lastId") Long lastId,
            Pageable pageable);
}
