package com.example.service;

import com.example.model.ChatGroupModel;
import com.example.model.MessageModel;
import com.example.model.UserLogin;
import com.example.repository.ChatGroupRepository;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;
import com.example.util.ApiResponse;
import com.example.util.GroupNotFoundException;
import com.example.util.InvalidInputException;
import com.example.util.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a new chat group with the given name and the creator's user ID.
     * @param groupName The name of the new chat group.
     * @param creatorId The user ID of the group creator.
     * @return The created ChatGroupModel.
     * @throws InvalidInputException If the group name is empty.
     * @throws UserNotFoundException If the user with the provided ID is not found.
     */
    @Transactional
    public ChatGroupModel createGroup(String groupName,String groupdescription,Long creatorId) throws InvalidInputException, UserNotFoundException {
        if (groupName == null || groupName.trim().isEmpty()) {
            logger.info("Group name cannot be empty");
            throw new InvalidInputException("Group name cannot be empty");
        }

        UserLogin creator = userRepository.findById(creatorId)
                .orElseThrow(() -> {
                    logger.info("User not found with ID: {}", creatorId);
                    return new UserNotFoundException("User not found");
                });

        ChatGroupModel group = new ChatGroupModel();
        group.setGroupName(groupName);
        group.setGroupDescription(groupdescription);
        group.setGroupMembers(new HashSet<>(Collections.singleton(creator))); // Add the creator to the group
        logger.info("Created new chat group: {}", group);
        return chatGroupRepository.save(group);
    }

    /**
     * Sends a message in the specified chat group.
     * @param groupId The ID of the chat group.
     * @param senderId The ID of the user sending the message.
     * @param message The content of the message.
     * @return The created MessageModel.
     * @throws InvalidInputException If the message is empty.
     * @throws GroupNotFoundException If the group with the provided ID is not found.
     * @throws UserNotFoundException If the user with the provided ID is not found.
     */
    @Transactional
    public MessageModel sendMessage(Long groupId, Long senderId, String message) throws InvalidInputException, GroupNotFoundException, UserNotFoundException {
        if (message == null || message.trim().isEmpty()) {
            logger.info("Message cannot be empty");
            throw new InvalidInputException("Message cannot be empty");
        }

        ChatGroupModel group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    logger.info("Group not found with ID: {}", groupId);
                    return new GroupNotFoundException("Group not found");
                });

        UserLogin sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    logger.info("User not found with ID: {}", senderId);
                    return new UserNotFoundException("User not found");
                });

        MessageModel newMessage = new MessageModel();
        newMessage.setMessage(message);
        newMessage.setSender(sender);
        newMessage.setChatGroup(group); // Sets both sides of the relationship
        logger.info("New message sent in group {}: {}", group.getGroupName(), newMessage);
        return messageRepository.save(newMessage);
    }

    /**
     * Retrieves the messages for the specified chat group.
     * @param groupId The ID of the chat group.
     * @return The list of MessageModel objects.
     * @throws GroupNotFoundException If the group with the provided ID is not found.
     */
    public List<MessageModel> getMessagesByGroupId(Long groupId) throws GroupNotFoundException {
        ChatGroupModel group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    logger.info("Group not found with ID: {}", groupId);
                    return new GroupNotFoundException("Group not found");
                });

        logger.info("Fetching messages for group {}", group.getGroupName());
        return messageRepository.findAllByChatGroupGroupId(groupId);
    }

    /**
     * Adds a user to the specified chat group.
     * @param groupId The ID of the chat group.
     * @param userId The ID of the user to be added.
     * @throws GroupNotFoundException If the group with the provided ID is not found.
     * @throws UserNotFoundException If the user with the provided ID is not found.
     */
    @Transactional
    public void joinGroup(Long groupId, Long userId) throws GroupNotFoundException, UserNotFoundException {
        ChatGroupModel group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> {
                    logger.info("Group not found with ID: {}", groupId);
                    return new GroupNotFoundException("Group not found");
                });

        UserLogin user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.info("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });

        if (group.getGroupMembers().contains(user)) {
            logger.info("User {} is already a member of group {}", user.getName(), group.getGroupName());
            return;
        }

        group.getGroupMembers().add(user);
        logger.info("User {} joined group {}", user.getName(), group.getGroupName());
    }

    /**
     * Retrieves the chat groups that the specified user is a member of.
     * @param userId The ID of the user.
     * @return The set of ChatGroupModel objects.
     * @throws UserNotFoundException If the user with the provided ID is not found.
     */
    public Set<ChatGroupModel> getJoinedChatGroups(Long userId) throws UserNotFoundException {
        UserLogin user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.info("User not found with ID: {}", userId);
                    return new UserNotFoundException("User not found");
                });
        logger.info("Fetching chat groups for user {}", user.getName());
        return user.getChatGroups();
    }
    
    public List<ChatGroupModel> getAllOtherChatGroupsWhereUserNotJoined(Long userid) throws UserNotFoundException {
		
    	List<ChatGroupModel> allChats = getAllChatGroups();
    	Set<ChatGroupModel> allJoinedChatGroupModels=new HashSet<ChatGroupModel>();
		logger.info("All Chats length: "+allChats.size());
		allJoinedChatGroupModels = getJoinedChatGroups(userid);
		
		logger.info("All allJoinedChatGroupModels length: "+allJoinedChatGroupModels.size());

	    allChats.removeAll(allJoinedChatGroupModels);
		logger.info("All Chats after length: "+allChats.size());

	    return allChats;
	}
    
    private List<ChatGroupModel> getAllChatGroups(){
    	return chatGroupRepository.findAll();
    }
    
    
    
    public ResponseEntity<ApiResponse> leaveGroupChat(Long groupId, Long userId) {
        try {
            ChatGroupModel chatGroup = chatGroupRepository.findById(groupId)
                    .orElseThrow(() -> {
                        logger.info("Group not found with ID: {}", groupId);
                        return new GroupNotFoundException("Group not found");
                    });

            UserLogin user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.info("User not found with ID: {}", userId);
                        return new UserNotFoundException("User not found");
                    });

            if (!chatGroup.getGroupMembers().contains(user)) {
                logger.info("User {} is not a member of group {}", user.getName(), chatGroup.getGroupName());
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("User is not a member of the group",false));
            }

            chatGroup.getGroupMembers().remove(user);
            chatGroupRepository.save(chatGroup);

            logger.info("User {} has left group {}", user.getName(), chatGroup.getGroupName());
            return ResponseEntity.ok(new ApiResponse("User has left the group successfully",true));
        } catch (GroupNotFoundException | UserNotFoundException e) {
            logger.error("Error occurred while leaving group chat: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(),false));
        } catch (Exception e) {
            logger.error("Unexpected error occurred while leaving group chat: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Unexpected error occurred",false));
        }
    }

	
}