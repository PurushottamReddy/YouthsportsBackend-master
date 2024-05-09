package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.MessageModel;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, Long> {
    List<MessageModel> findAllByChatGroupGroupId(Long groupId);
}
