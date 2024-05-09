package com.example.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class NotificationModel {

	@Id
	@Column(name ="notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	
	private Date notifiedTime;
	
	
	@Column(name="notification_message")
	private String notificationMessage;
	
	@Column(name="event_Type")
	private EventType eventType;
	
	

	public NotificationModel() {
		super();
	}

	public NotificationModel(Long id, String notificationMessage, EventType eventType) {
		super();
		this.id = id;
		this.notificationMessage = notificationMessage;
		this.eventType = eventType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	

	@Override
	public String toString() {
		return "NotificationModel [id=" + id + ", notificationMessage=" + notificationMessage + ", eventType="
				+ eventType + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventType, id, notificationMessage);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationModel other = (NotificationModel) obj;
		return eventType == other.eventType && Objects.equals(id, other.id)
				&& Objects.equals(notificationMessage, other.notificationMessage);
	}
	
	
}
