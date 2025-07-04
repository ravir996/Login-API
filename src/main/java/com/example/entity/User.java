package com.example.entity;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table(value = "user")
@EqualsAndHashCode(callSuper = false)
public class User {
	
	@Id
	@Column("id")
	private Long id;
	
	@Column("name")
	private String userName;
	
	@Column("password")
	private String password;
	
	@Column("email_id")
	private String emailId;
	
	@Column("mobile_no")
	private String mobileNo;
	
	@Column("status")
	private String status;
	
	@Column("stage")
	private String stage;
	
	@Column("otp")
	private String otp;

	@Column("expiry_time")
	private LocalTime expiryTime;
	
	

}
