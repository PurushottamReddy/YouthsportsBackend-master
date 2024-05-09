package com.example.service;

import com.example.model.AchievementModel;
import com.example.model.UserLogin;
import com.example.repository.AchievementRepository;
import com.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserRepository userRepository;
    
    public AchievementModel createAchievement(AchievementModel achievement) {
        return achievementRepository.save(achievement);
    }

    public List<AchievementModel> getAllAchievements(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
            .map(user -> {
                List<AchievementModel> list = achievementRepository.findByAchievedUser(user);
                list.forEach(achievement -> System.out.println(achievement));
                System.out.println("Total achievements found: " + list.size());
                return list;
            })
            .orElseGet(() -> {
                System.out.println("No user found with email: " + userEmail);
                return Collections.emptyList(); // Return an empty list if user is not found
            });
    }


    public AchievementModel getAchievementById(Long id) {
        return achievementRepository.findById(id).orElse(null);
    }

    public AchievementModel updateAchievement(Long id, AchievementModel updatedAchievement) {
        return achievementRepository.findById(id).map(achievement -> {
            achievement.setTitle(updatedAchievement.getTitle());
            achievement.setDescription(updatedAchievement.getDescription());
            achievement.setAwardedOn(updatedAchievement.getAwardedOn());
            achievement.setAchievedUser(updatedAchievement.getAchievedUser());
//            achievement.setAwardedUser(updatedAchievement.getAwardedUser());
            return achievementRepository.save(achievement);
        }).orElse(null);
    }

    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }
}
