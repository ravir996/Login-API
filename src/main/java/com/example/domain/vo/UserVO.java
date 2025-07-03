package com.example.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVO {
	
	private Long id;
	private String name;
	private String emailId;
	private String mobileNo;
	private String password;
	private String confrmPassword;
	private String message;

}
