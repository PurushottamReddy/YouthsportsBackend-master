package com.example.repository;

import com.example.model.AchievementModel;
import com.example.model.UserLogin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AchievementRepository extends JpaRepository<AchievementModel, Long> {
    
	List<AchievementModel> findByAchievedUser(UserLogin achievedUser);

}
