package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.ChatGroupModel;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroupModel, Long> {

}
