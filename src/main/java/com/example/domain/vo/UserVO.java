package com.example.domain.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO {
	
	private Long id;
	private String userName;
	private String emailId;
	private String mobileNo;
	private String password;
	private String confrmPassword;
	private String otp;
	@JsonIgnore
	private LocalDateTime expiryTime;
	private String message;

}
