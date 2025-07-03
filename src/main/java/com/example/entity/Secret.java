package com.example.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table(value = "secret")
@EqualsAndHashCode(callSuper = false)
public class Secret {
	
	@Id
	@Column("id")
	private Long id;
	
	@Column("token")
	private String token;

}
