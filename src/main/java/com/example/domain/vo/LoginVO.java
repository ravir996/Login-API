package com.example.domain.vo;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginVO {
	
	private Long id;
    private String username;
    private String password;
    private String newPassword;
    private String confrmPassword;

}

