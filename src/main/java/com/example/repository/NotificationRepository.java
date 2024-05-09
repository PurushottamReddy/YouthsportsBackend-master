package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.NotificationModel;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long>{

}
