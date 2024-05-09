package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.NotificationModel;
import com.example.repository.NotificationRepository;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepo;
	
	public List<NotificationModel> getNotifications(){
		
		return null;
	}
}
