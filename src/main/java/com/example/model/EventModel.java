package com.example.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

@Entity
@Table(name = "events")
public class EventModel {

    @Id
	@Column(name ="event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "event_start_date")
    private Date eventStartDate;
    
    @Column(name = "event_end_date")
    private Date eventEndDate;

    @Column(name = "type")
    private EventType type; // Could be "schedule", "practice", or "event"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private UserLogin createdUser; // Assuming UserLogin is your user entity
    
    
    

	public EventModel() {
		super();
	}

	public EventModel(Long id, String title, String description, Date eventStartDate, Date eventEndDate, EventType type) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Date getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public Date getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public UserLogin getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(UserLogin createdUser) {
		this.createdUser = createdUser;
	}

	@Override
	public String toString() {
		return "EventModel [id=" + id + ", title=" + title + ", description=" + description + ", eventStartDate="
				+ eventStartDate + ", eventEndDate=" + eventEndDate + ", type=" + type + ", createdUser=" + createdUser
				+ "]";
	}

    
}
