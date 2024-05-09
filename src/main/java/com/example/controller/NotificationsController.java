package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.EventType;
import com.example.model.NotificationModel;
import com.example.service.NotificationService;

@RestController
public class NotificationsController {

	@Autowired
	private NotificationService notificationService;
	
	public List<NotificationModel> getNotifications(){
		
		return null;
	}
}
