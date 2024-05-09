package com.example.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;


@Entity
public class MessageModel {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_group_id")
    private ChatGroupModel chatGroup;

    @JsonIgnore
    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserLogin sender;

    @Column(name = "message")
    private String message;

    @Column(name = "timestamp")
    private Date timestamp = new Date();

    
    public MessageModel() {
		super();
	}

	public MessageModel(String message) {
		super();
		this.message = message;
	}
	
	

	public MessageModel(ChatGroupModel chatGroup, UserLogin sender, String message) {
		super();
		this.chatGroup = chatGroup;
		this.sender = sender;
		this.message = message;
	}

	public MessageModel(Long id, ChatGroupModel chatGroup, UserLogin sender, String message, Date timestamp) {
		super();
		this.id = id;
		this.chatGroup = chatGroup;
		this.sender = sender;
		this.message = message;
		this.timestamp = timestamp;
	}

	public void setChatGroup(ChatGroupModel chatGroup) {
        this.chatGroup = chatGroup;
        chatGroup.getMessages().add(this);
    }

    public void setSender(UserLogin sender) {
        this.sender = sender;
        sender.getMessages().add(this);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public ChatGroupModel getChatGroup() {
		return chatGroup;
	}

	public UserLogin getSender() {
		return sender;
	}
	@JsonGetter("senderId")
    public Long getSenderId() {
        return this.sender != null ? this.sender.getUserId() : null;
    }
	@JsonGetter("senderName")
	public String getSenderName() {
		return this.sender != null ? this.sender.getName() : null;
	}


	
    
    
}
