package com.blog.repository;

import com.blog.entity.Post;
import com.blog.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findByUser(User user);

        Optional<Post> findByUuid(String uuid);

        @Query("""
                          SELECT p FROM Post p
                          WHERE p.user.id IN (
                            SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId
                          )
                          ORDER BY p.timestamp DESC
                        """)
        List<Post> findPostsFromFollowedUsers(@Param("userId") long userId);

        @Transactional
        void deleteByUuid(String uuid);
}
