package com.blog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotifRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByNotificatedUserOrderByIdDesc(String uuid);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n set n.isRead = true WHERE n.uuid = :uuid")
    void markAsRead(@Param("uuid") String uuid);

    long countByNotificatedUserAndIsReadFalse(String uuid);

    List<Notification> findByNotificatedUserAndIsReadFalse(String uuid);

    @Query("""
                select COUNT(n) from Notification n
                left join Post p 
                on n.postOrProfileUuid = p.uuid where n.notificatedUser = :userUuid 
                and n.isRead = false 
                and (p.status is NULL or p.status != 'HIDE')
            """)
    long countUnreadNotificationsExcludingHiddenPosts(@Param("userUuid") String userUuid);

    @Transactional
    @Modifying
    void deleteByNotificatedUserAndNotificationOwner(String crrUser, String otherUser);

    @Query("""
            SELECT n FROM Notification n
            LEFT JOIN Post p ON n.postOrProfileUuid = p.uuid
            WHERE n.notificatedUser = :userUuid
            AND (p.status IS NULL OR p.status != 'HIDE')
            AND (:lastTime IS NULL OR n.createdAt < :lastTime
            OR (n.createdAt = :lastTime AND n.id < :lastId))
            ORDER BY n.createdAt DESC
        """)
    List<Notification> findByNotificatedUserPaginated(
        @Param("userUuid") String userUuid,
        @Param("lastTime") Long lastTime,
        @Param("lastId") Long lastId,
        Pageable pageable);
}
