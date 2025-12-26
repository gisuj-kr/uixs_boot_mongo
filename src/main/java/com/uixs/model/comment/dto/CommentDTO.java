package com.uixs.model.comment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "comment")
public class CommentDTO {
	@Id
	private String id;
	
	private String ref_id;	// 관계 작업요청 아이디
	private String writer_type;
	private String writer;
	private String writer_name;
	private String content;
	
	@DateTimeFormat(pattern="yyyy-MM-ddTHH:mm:ss")
	private LocalDateTime regdate = LocalDateTime.now();
}
