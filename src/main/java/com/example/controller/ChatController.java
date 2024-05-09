package com.example.controller;

import com.example.model.ChatGroupModel;
import com.example.model.MessageModel;
import com.example.service.ChatService;
import com.example.util.ApiResponse;
import com.example.util.GroupNotFoundException;
import com.example.util.InvalidInputException;
import com.example.util.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    // Logger for logging messages
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    /**
     * Creates a new chat group with the specified name and the creator user.
     *
     * @param groupname  the name of the new chat group
     * @param creatorid  the ID of the user creating the group
     * @return the newly created ChatGroupModel
     */
    @PostMapping("/creategroup")
    public ResponseEntity<ApiResponse> createGroup(@RequestParam String groupname,@RequestParam String groupdescription, @RequestParam Long creatorid) {
        try {
            // Create a new chat group
            ChatGroupModel newGroup = chatService.createGroup(groupname,groupdescription, creatorid);
            logger.info("New chat group created: {}", newGroup);
            return ResponseEntity.ok(new ApiResponse("New Chat Group is created with group id: "+newGroup.getGroupId(),true));
        } catch (InvalidInputException e) {
            logger.error("Error creating chat group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (UserNotFoundException e) {
            logger.error("Error creating chat group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error creating chat group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Sends a new message to the specified chat group.
     *
     * @param groupid   the ID of the chat group
     * @param senderid  the ID of the user sending the message
     * @param message   the content of the message
     * @return the newly created MessageModel
     */
    @PostMapping("/sendmessage")
    public ResponseEntity<MessageModel> sendMessage(@RequestParam Long groupid, @RequestParam Long senderid, @RequestParam String message) {
        try {
            // Send a message to the specified chat group
            MessageModel newMessage = chatService.sendMessage(groupid, senderid, message);
            logger.info("New message sent in group {}: {}", newMessage.getChatGroup().getGroupName(), newMessage);
            
            return ResponseEntity.ok(newMessage);
        } catch (GroupNotFoundException e) {
            logger.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UserNotFoundException e) {
            logger.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (InvalidInputException e) {
            logger.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Unexpected error sending message: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves all the messages for the specified chat group.
     *
     * @param groupid the ID of the chat group
     * @return the list of MessageModel objects for the given group
     */
    @GetMapping("/getmessages")
    public ResponseEntity<List<MessageModel>> getMessagesByGroupId(@RequestParam Long groupid) {
        try {
            // Retrieve messages for the specified chat group
            List<MessageModel> messages = chatService.getMessagesByGroupId(groupid);
            logger.info("Fetched {} messages for group with ID {}", messages.size(), groupid);
            return ResponseEntity.ok(messages);
        } catch (GroupNotFoundException e) {
            logger.error("Error fetching messages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error fetching messages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Adds a user to the specified chat group.
     *
     * @param groupid the ID of the chat group
     * @param userid  the ID of the user to be added
     * @return a successful response if the user was added to the group
     */
    @PostMapping("/joingroup")
    public ResponseEntity<ApiResponse> joinGroup(@RequestParam Long groupid, @RequestParam Long userid) {
        try {
            // Add a user to the specified chat group
            chatService.joinGroup(groupid, userid);
            logger.info("User with ID {} joined group with ID {}", userid, groupid);
            return ResponseEntity.ok().body(new ApiResponse("User with id"+userid+" able to join group id: "+groupid,true));
        } catch (GroupNotFoundException e) {
            logger.error("Error joining group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User with id"+userid+" is unable to join group id: "+groupid+" message: "+e.getMessage(),false));
        } catch (UserNotFoundException e) {
            logger.error("Error joining group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("User with id"+userid+" is unable to join group id: "+groupid+" message: "+e.getMessage(),false));
        } catch (Exception e) {
            logger.error("Unexpected error joining group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("User with id"+userid+" is unable to join group id: "+groupid+" message: "+e.getMessage(),false));
        }
    }

    /**
     * Retrieves all the chat groups that the specified user belongs to.
     *
     * @param userid the ID of the user
     * @return the set of ChatGroupModel objects that the user is a member of
     */
    @GetMapping("/getjoinedchatgroups")
    public ResponseEntity<Set<ChatGroupModel>> getJoinedChatGroups(@RequestParam Long userid) {
        try {
            // Retrieve chat groups that the user belongs to
            Set<ChatGroupModel> groups = chatService.getJoinedChatGroups(userid);
            logger.info("Fetched {} chat groups for user with ID {}", groups.size(), userid);
            return ResponseEntity.ok(groups);
        } catch (UserNotFoundException e) {
            logger.error("Error fetching user groups: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error fetching user groups: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/getallnotjoinedgroupchats")
    public ResponseEntity<List<ChatGroupModel>> getAllOtherChatGroupsWhereUserNotJoined(@RequestParam Long userid){
    	
        try {
            // Retrieve chat groups that the user belongs to
            List<ChatGroupModel> groups = chatService.getAllOtherChatGroupsWhereUserNotJoined(userid);
            logger.info("Fetched {} chat groups for user with ID {}", groups.size(), userid);
            return ResponseEntity.ok(groups);
        } catch (UserNotFoundException e) {
            logger.error("Error fetching user groups: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error fetching user groups: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    	
     }
    
    /**
     * Endpoint for leaving a chat group.
     *
     * @param groupid the ID of the chat group
     * @param userid  the ID of the user leaving the group
     * @return a response indicating the success or failure of leaving the group
     */
    @PutMapping("/leavegroup")
    public ResponseEntity<ApiResponse> leaveGroupChat(@RequestParam Long groupid,Long userid){
    	return chatService.leaveGroupChat(groupid, userid);
    }
}
