package com.example.service;

import com.example.model.ChatGroupModel;
import com.example.model.MessageModel;
import com.example.model.UserLogin;
import com.example.repository.ChatGroupRepository;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;
import com.example.util.GroupNotFoundException;
import com.example.util.InvalidInputException;
import com.example.util.UserNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatGroupRepository chatGroupRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    private UserLogin user;
    private ChatGroupModel group;

    @BeforeEach
    void setup() {
        user = new UserLogin();
        user.setUserId(1L);
        user.setName("John Doe");

        group = new ChatGroupModel(1L, "Fun Group", "A group for fun");
        group.getGroupMembers().add(user);
    }

    @Test
    void createGroup_ValidInputs_ReturnsNewGroup() throws Exception {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(chatGroupRepository.save(any(ChatGroupModel.class))).thenReturn(group);

        ChatGroupModel createdGroup = chatService.createGroup("Fun Group", "A group for fun", 1L);
        
        assertNotNull(createdGroup);
        assertEquals("Fun Group", createdGroup.getGroupName());
        assertTrue(createdGroup.getGroupMembers().contains(user));
        verify(chatGroupRepository).save(any(ChatGroupModel.class));
    }

    @Test
    void createGroup_InvalidGroupName_ThrowsInvalidInputException() {
    	InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            chatService.createGroup("", "A group for fun", 1L);
        });

//        System.out.println("Group name cannot be emptyy "+ exception.getErrorMessage());
        assertEquals("Group name cannot be empty", exception.getErrorMessage());
    }

    @Test
    void sendMessage_ValidInputs_ReturnsNewMessage() throws Exception {
        when(chatGroupRepository.findById(anyLong())).thenReturn(Optional.of(group));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(messageRepository.save(any(MessageModel.class))).thenReturn(new MessageModel(group, user, "Hello!"));

        MessageModel newMessage = chatService.sendMessage(1L, 1L, "Hello!");

        assertNotNull(newMessage);
        assertEquals("Hello!", newMessage.getMessage());
        assertEquals(user, newMessage.getSender());
        verify(messageRepository).save(any(MessageModel.class));
    }

    @Test
    void joinGroup_UserNotFound_ThrowsUserNotFoundException() {
        when(chatGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            chatService.joinGroup(1L, 2L);
        });

        assertEquals("User not found", exception.getErrorMessage());
    }
    
    @Test
    void joinGroup_GroupNotFound_ThrowsGroupNotFoundException() {
        when(chatGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        GroupNotFoundException exception = assertThrows(GroupNotFoundException.class, () -> {
            chatService.joinGroup(1L, 1L);
        });

        assertEquals("Group not found", exception.getErrorMessage());
    }
    @Test
    void joinGroup_UserAlreadyMember_DoesNotAddUser() throws Exception {
        when(chatGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        chatService.joinGroup(1L, 1L);

        verify(chatGroupRepository, never()).save(group);
        assertEquals(1, group.getGroupMembers().size());
    }
}
