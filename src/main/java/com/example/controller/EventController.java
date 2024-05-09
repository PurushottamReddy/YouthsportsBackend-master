package com.example.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.EventModel;
import com.example.model.EventType;
import com.example.service.EventService;
import com.example.service.UserService;
import com.example.util.ApiResponse;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;

    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    /**
     * Retrieves filtered events based on type, start date, end date, and user's authorization token.
     *
     * @param type      the type of event (optional)
     * @param startDate the start date of the event (optional)
     * @param endDate   the end date of the event (optional)
     * @param headers   the HTTP headers containing user's authorization token
     * @return ResponseEntity containing the list of filtered events
     */
    @GetMapping("/getevents")
    public ResponseEntity<List<EventModel>> getEvents(
            @RequestParam(required = false) EventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestHeader HttpHeaders headers) {
        
        // Extract user email from authorization token
        String userEmail = userService.getUserEmailFromToken(headers.getFirst("Authorization"));
        
        // Retrieve filtered events based on user email and filter criteria
        List<EventModel> events = eventService.getFilteredEvents(userEmail, type, startDate, endDate);
        
        // Log event retrieval
        logger.info("Retrieved {} events for user '{}'", events.size(), userEmail);
        
        // Return the list of events
        return ResponseEntity.ok(events);
    }
    
    /**
     * Retrieves events for preview based on type, limit, page, and user's authorization token.
     *
     * @param type    the type of event
     * @param limit   the maximum number of events to retrieve
     * @param page    the page number
     * @param headers the HTTP headers containing user's authorization token
     * @return ResponseEntity containing the list of events for preview
     */
    @GetMapping("/geteventsforpreview")
    public ResponseEntity<List<EventModel>> getEventsForPreview(@RequestParam("type") EventType type, @RequestParam("limit") int limit, @RequestParam("page") int page, @RequestHeader HttpHeaders headers) {
        try {
            // Extract user email from authorization token
            String token = headers.getFirst("Authorization");
            String userEmail = userService.getUserEmailFromToken(token);
            
            // Retrieve events for preview based on user email, event type, page, and limit
            List<EventModel> events = eventService.getAllEventsForPreview(userEmail, type, page, limit);
            
            // Log event retrieval for preview
            logger.info("Retrieved {} events for preview for user '{}'", events.size(), userEmail);
            
            // Return the list of events for preview
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            // Log and return internal server error in case of exception
            logger.error("Error retrieving events for preview: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Adds a new event.
     *
     * @param event   the event to be added
     * @param headers the HTTP headers containing user's authorization token
     * @return ResponseEntity containing the API response
     */
    @PostMapping("/addevent")
    public ResponseEntity<ApiResponse> addEvent(@RequestBody EventModel event, @RequestHeader HttpHeaders headers) {
        try {
            // Extract user email from authorization token
            String userEmail  = userService.getUserEmailFromToken(headers.getFirst("Authorization"));
            
            // Determine the account type based on user email
            String accountType = userService.getAccountTypeBasedOnEmail(userEmail);
            
            // Check if the user is authorized to add an event
            if (!"Coach".equals(accountType)) {
                // If not a coach, return forbidden access status
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Add the event
            ResponseEntity<ApiResponse> response = eventService.addEvent(userEmail, event, accountType);
            
            // Log event addition
            logger.info("Added new event by user '{}': {}", userEmail, event);
            
            // Return the response
            return response;
        } catch (Exception e) {
            // Log and return internal server error in case of exception
            logger.error("Error adding event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
