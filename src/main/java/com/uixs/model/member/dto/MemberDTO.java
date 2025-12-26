package com.uixs.model.member.dto;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "member")
public class MemberDTO {
	@Id
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
