package com.example.controller;

import com.example.model.AchievementModel;
import com.example.service.AchievementService;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/createachievement")
    public ResponseEntity<AchievementModel> createAchievement(@RequestBody AchievementModel achievement) {
        return ResponseEntity.ok(achievementService.createAchievement(achievement));
    }

    @GetMapping("/getallachievements")
    public ResponseEntity<List<AchievementModel>> getAllAchievements(@RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = userService.getUserEmailFromToken(token); // Remove "Bearer " prefix
        System.out.println(userEmail);
        List<AchievementModel> achievementList = achievementService.getAllAchievements(userEmail);
        return ResponseEntity.ok(achievementList);
    }


    @GetMapping("/getachievement/{id}")
    public ResponseEntity<AchievementModel> getAchievementById(@PathVariable Long id) {
        AchievementModel achievement = achievementService.getAchievementById(id);
        return achievement != null ? ResponseEntity.ok(achievement) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updateachievement/{id}")
    public ResponseEntity<AchievementModel> updateAchievement(@PathVariable Long id, @RequestBody AchievementModel achievement) {
        AchievementModel updatedAchievement = achievementService.updateAchievement(id, achievement);
        return updatedAchievement != null ? ResponseEntity.ok(updatedAchievement) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deleteachievement/{id}")
    public ResponseEntity<?> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.ok().build();
    }
}
