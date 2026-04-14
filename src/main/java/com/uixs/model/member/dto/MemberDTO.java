package com.uixs.model.member.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class MemberDTO {
	private String id;
	
	private String userid;
	private String username;
	private String password;
	private String team;
	private String part;
	private String tel;
	private String email;
	private String auth;
	
	@DateTimeFormat(pattern="yyyy-MM-ddTHH:mm:ss")
	private LocalDateTime reg_date = LocalDateTime.now();
}
