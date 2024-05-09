package com.example.repository;

import com.example.model.EventModel;
import com.example.model.EventType;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventModel, Long> {

    @Query("SELECT e FROM EventModel e WHERE e.createdUser.userEmail = ?1 AND e.type = ?2 AND e.eventStartDate >= ?3 AND e.eventEndDate <= ?4")
    List<EventModel> findByUserEmailAndTypeAndDateRange(String userEmail, EventType type, Date startDate, Date endDate);

    @Query("SELECT e FROM EventModel e WHERE (e.createdUser.userEmail = ?1 AND e.type = ?2) OR (e.eventStartDate >= ?3 AND e.eventEndDate <= ?4)")
    List<EventModel> findByUserEmailAndTypeOrDateRange(String userEmail, EventType type, Date startDate, Date endDate);

    @Query("SELECT e FROM EventModel e WHERE e.createdUser.userEmail = ?1 OR e.type = ?2 OR (e.eventStartDate >= ?3 AND e.eventEndDate <= ?4)")
    List<EventModel> findByUserEmailOrTypeOrDateRange(String userEmail, EventType type, Date startDate, Date endDate);

    @Query("SELECT e FROM EventModel e WHERE e.type = ?1 AND e.eventStartDate >= ?2 AND e.eventEndDate <= ?3")
    List<EventModel> findByTypeAndDateRange(EventType type, Date startDate, Date endDate);

    @Query("SELECT e FROM EventModel e WHERE e.type = ?1 OR e.eventStartDate >= ?2 OR e.eventEndDate <= ?3")
    List<EventModel> findByTypeOrDateRange(EventType type, Date startDate, Date endDate);
    
    @Query("SELECT e FROM EventModel e WHERE (:type IS NULL OR e.type = :type) AND (:userEmail IS NULL OR e.createdUser.userEmail = :userEmail) ORDER BY e.eventStartDate DESC")
    List<EventModel> findByUserEmailAndType(@Param("userEmail") String userEmail, @Param("type") EventType type, Pageable pageable);

    @Query("SELECT e FROM EventModel e WHERE e.type = :type ORDER BY e.eventStartDate DESC")
    List<EventModel> findByType(@Param("type") EventType type, Pageable pageable);



}

