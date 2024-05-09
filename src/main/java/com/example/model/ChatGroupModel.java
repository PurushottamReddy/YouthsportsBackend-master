package com.example.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;


@Entity
public class ChatGroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(name = "group_name")
    private String groupName;
    
    @Column(name = "group_description")
    private String groupDescription;

    @JsonIgnore
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
        name = "chat_group_members",
        joinColumns = @JoinColumn(name = "chat_group_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserLogin> groupMembers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "chatGroup",fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<MessageModel> messages = new HashSet<>();

    
    public ChatGroupModel() {
		super();
	}


	public ChatGroupModel(long groupId,String groupName, String groupDescription) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupDescription = groupDescription;
	}

    
	public void addMessage(MessageModel message) {
        messages.add(message);
        message.setChatGroup(this);
    }

    public void addGroupMember(UserLogin user) {
        groupMembers.add(user);
        user.getChatGroups().add(this);
    }

    public void removeGroupMember(UserLogin user) {
        groupMembers.remove(user);
        user.getChatGroups().remove(this);
    }

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public Set<UserLogin> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<UserLogin> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set<MessageModel> getMessages() {
		return messages;
	}

	public void setMessages(Set<MessageModel> messages) {
		this.messages = messages;
	}

//	@Override
//	public String toString() {
//		return "ChatGroupModel [groupId=" + groupId + ", groupName=" + groupName + ", groupDescription="
//				+ groupDescription + ", groupMembers=" + groupMembers + ", messages=" + messages + "]";
//	}

	
	
    
}