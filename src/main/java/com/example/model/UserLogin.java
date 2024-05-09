package com.example.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.JoinColumn;

@Entity
@Table(name = "user_login")
public class UserLogin {
    @Id
    @Column(name = "userid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "userEmail", unique = true)
    private String userEmail;

    @Column(name = "password")
    private String password; // Store hashed password with salt

    @Column(name = "contactNumber")
    private String contactNumber;

    @JsonIgnore
    @Column(name = "is_user_verified")
    private Boolean isUserVerified;

    @JsonIgnore
    @Column(name = "created_timestamp")
    private Date createdTimestamp;

    @JsonIgnore
    @Column(name = "last_login_timestamp")
    private Date lastLoginTimestamp;

    @Column(name = "account_type")
    private AccountType accountType;

    @JsonIgnore
    @Column(name = "email_link_for_verification")
    private String emailVerificationToken;

    @JsonIgnore
    @Column(name = "email_link_verification_expiry_date")
    private Date emailVerificationTokenExpiry;

    @JsonIgnore
    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @JsonIgnore
    @Column(name = "password_reset_token_expiry")
    private Date passwordResetTokenExpiry;

    @JsonIgnore
    @ManyToMany(mappedBy = "groupMembers",fetch = FetchType.LAZY ,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ChatGroupModel> chatGroups = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MessageModel> messages = new HashSet<>();
    
    

	public UserLogin() {
		super();
	}

	public UserLogin(Long userId, String name) {
		super();
		this.userId = userId;
		this.name = name;
	}
	

	public UserLogin(String userEmail, String password, Boolean isUserVerified) {
		super();
		this.userEmail = userEmail;
		this.password = password;
		this.isUserVerified = isUserVerified;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Boolean getIsUserVerified() {
		return isUserVerified;
	}

	public void setIsUserVerified(Boolean isUserVerified) {
		this.isUserVerified = isUserVerified;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Date getLastLoginTimestamp() {
		return lastLoginTimestamp;
	}

	public void setLastLoginTimestamp(Date lastLoginTimestamp) {
		this.lastLoginTimestamp = lastLoginTimestamp;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Date getEmailVerificationTokenExpiry() {
		return emailVerificationTokenExpiry;
	}

	public void setEmailVerificationTokenExpiry(Date emailVerificationTokenExpiry) {
		this.emailVerificationTokenExpiry = emailVerificationTokenExpiry;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public Date getPasswordResetTokenExpiry() {
		return passwordResetTokenExpiry;
	}

	public void setPasswordResetTokenExpiry(Date passwordResetTokenExpiry) {
		this.passwordResetTokenExpiry = passwordResetTokenExpiry;
	}

	public Set<ChatGroupModel> getChatGroups() {
		return chatGroups;
	}

	public void setChatGroups(Set<ChatGroupModel> chatGroups) {
		this.chatGroups = chatGroups;
	}

	public Set<MessageModel> getMessages() {
		return messages;
	}

	public void setMessages(Set<MessageModel> messages) {
		this.messages = messages;
	}

	@Override
	public String toString() {
		return "UserLogin [userId=" + userId + ", name=" + name + ", userEmail=" + userEmail + ", password=" + password
				+ ", contactNumber=" + contactNumber + ", isUserVerified=" + isUserVerified + ", createdTimestamp="
				+ createdTimestamp + ", lastLoginTimestamp=" + lastLoginTimestamp + ", accountType=" + accountType
				+ ", emailVerificationToken=" + emailVerificationToken + ", emailVerificationTokenExpiry="
				+ emailVerificationTokenExpiry + ", passwordResetToken=" + passwordResetToken
				+ ", passwordResetTokenExpiry=" + passwordResetTokenExpiry + ", chatGroups=" + chatGroups
				+ ", messages=" + messages + "]";
	}

    	
    
}