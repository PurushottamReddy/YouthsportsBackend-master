package com.example.service;

import com.example.model.EventModel;
import com.example.model.EventType;
import com.example.model.UserLogin;
import com.example.repository.EventRepository;
import com.example.repository.UserRepository;
import com.example.util.ApiResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepo;
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    
    public List<EventModel> getFilteredEvents(String userEmail, EventType type, Date startDate, Date endDate) {
        

        UserLogin user = userRepo.findByUserEmail(userEmail).orElse(null);
        if (user != null && !user.getAccountType().toString().contains("Coach") ) {
            userEmail = null;
        }
        
        logger.info("Filtering events with userEmail: {}, type: {}, startDate: {}, endDate: {}", userEmail, type, startDate, endDate);


        if (userEmail != null && type != null && startDate != null && endDate != null) {
            logger.info("Filtering events by userEmail, type, and date range");
            return eventRepository.findByUserEmailAndTypeAndDateRange(userEmail, type, startDate, endDate);
        } else if (userEmail != null && type != null) {
            logger.info("Filtering events by userEmail and type");
            return eventRepository.findByUserEmailAndTypeOrDateRange(userEmail, type, startDate, endDate);
        } else if (userEmail != null || type != null || (startDate != null && endDate != null)) {
            logger.info("Filtering events by userEmail or type or date range");
            return eventRepository.findByUserEmailOrTypeOrDateRange(userEmail, type, startDate, endDate);
        } else if (type != null && startDate != null && endDate != null) {
            logger.info("Filtering events by type and date range");
            return eventRepository.findByTypeAndDateRange(type, startDate, endDate);
        } else if (type != null || startDate != null || endDate != null) {
            logger.info("Filtering events by type or date range");
            return eventRepository.findByTypeOrDateRange(type, startDate, endDate);
        } else {
            logger.info("Returning all events");
            return eventRepository.findAll();
        }
    }
    
    public List<EventModel> getAllEventsForPreview(String userEmail, EventType type, int page, int limit) {
        logger.info("Starting getAllEventsForPreview with userEmail: {}, type: {}, page: {}, limit: {}", userEmail, type, page, limit);
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("eventStartDate").descending());
        logger.info("Pageable object created: {}", pageable);

        if (userEmail != null) {
            UserLogin user = userRepo.findByUserEmail(userEmail).orElse(null);
            if (user != null && "Coach".equals(user.getAccountType())) {
                logger.info("Fetching events for coach: {}", userEmail);
                return eventRepository.findByUserEmailAndType(userEmail, type, pageable);
            }
        }
        
        logger.info("Fetching all events of type: {}", type);
        List<EventModel> events = eventRepository.findByType(type, pageable);
        logger.info("Number of events fetched: {}", events.size());
        return events;
    }





    public ResponseEntity<ApiResponse> addEvent(String userEmail, EventModel event, String userType) {
        // Find the user by email
        UserLogin createdUser = userRepo.findByUserEmail(userEmail).orElse(null);

        // Check if the user exists and is a coach
        if (createdUser == null || !"Coach".equals(userType)) {
            // If user not found or not a coach, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(new ApiResponse("Only coaches are allowed to add events.", false));
        }

        // Set the created user for the event
        event.setCreatedUser(createdUser);

        try {
            // Attempt to save the event
            eventRepository.save(event);
            // Return success response if event creation is successful
            return ResponseEntity.ok(new ApiResponse("Event created successfully!", true));
        } catch (Exception ex) {
            // If there is any exception during event creation, return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ApiResponse("Failed to create event. Please try again later.", false));
        }
    }

}

