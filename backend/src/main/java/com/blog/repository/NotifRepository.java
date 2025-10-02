package com.blog.repository;

import java.beans.Transient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotifRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUuidOrderByIdDesc(String uuid);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n set n.isRead = true WHERE n.uuid = :uuid")
    void markAsRead(@Param("uuid") String uuid);

    long countByUserUuidAndIsReadFalse(String uuid);
}
