package com.example.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "achievements")
public class AchievementModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achieved_user_id")
    private UserLogin achievedUser;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "awarded_user_id")
//    private UserLogin awardedUser;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date awardedOn;

    

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserLogin getAchievedUser() {
        return achievedUser;
    }

    public void setAchievedUser(UserLogin achievedUser) {
        this.achievedUser = achievedUser;
    }

//    public UserLogin getAwardedUser() {
//        return awardedUser;
//    }
//
//    public void setAwardedUser(UserLogin awardedUser) {
//        this.awardedUser = awardedUser;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getAwardedOn() {
        return awardedOn;
    }

    public void setAwardedOn(Date awardedOn) {
        this.awardedOn = awardedOn;
    }

	@Override
	public String toString() {
		return "AchievementModel [id=" + id + ", achievedUser=" + achievedUser + ", title=" + title + ", description="
				+ description + ", awardedOn=" + awardedOn + "]";
	}

	

   
}
