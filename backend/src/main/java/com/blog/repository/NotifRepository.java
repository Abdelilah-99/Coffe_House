package com.blog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.blog.entity.*;

@Repository
public interface NotifRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserUuid(String uuid);
}
