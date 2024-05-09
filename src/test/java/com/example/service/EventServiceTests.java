package com.example.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.model.AccountType;
import com.example.model.EventModel;
import com.example.model.EventType;
import com.example.model.UserLogin;
import com.example.repository.EventRepository;
import com.example.repository.UserRepository;
import com.example.util.ApiResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class EventServiceTests {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testGetFilteredEvents_UserAndDateRange() {
        Date startDate = new Date();
        Date endDate = new Date();
        EventType type = EventType.Schedule;
        UserLogin user = new UserLogin();
        user.setAccountType(AccountType.Coach);

        List<EventModel> expectedEvents = Arrays.asList(new EventModel());
        when(userRepository.findByUserEmail("coach@example.com")).thenReturn(Optional.of(user));
        when(eventRepository.findByUserEmailAndTypeAndDateRange("coach@example.com", type, startDate, endDate)).thenReturn(expectedEvents);

        List<EventModel> actualEvents = eventService.getFilteredEvents("coach@example.com", type, startDate, endDate);

        assertEquals(expectedEvents, actualEvents);
    }
   
    @Test
    public void testAddEvent_ByCoach() {
        // Arrange
        EventModel newEvent = new EventModel();
        UserLogin coach = new UserLogin();
        coach.setAccountType(AccountType.Coach);
        when(userRepository.findByUserEmail("coach@example.com")).thenReturn(Optional.of(coach));
        when(eventRepository.save(any(EventModel.class))).thenReturn(newEvent);

        // Act
        ApiResponse response = eventService.addEvent("coach@example.com", newEvent, "Coach").getBody();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Event created successfully!", response.getMessage());
    }
}
