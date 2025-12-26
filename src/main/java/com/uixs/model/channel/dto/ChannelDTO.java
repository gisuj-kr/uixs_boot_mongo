package com.uixs.model.channel.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "channel")
public class ChannelDTO {
	@Id
	private String id;
	
	private String mongoId;
	private String code;
	private String name;
	private char useyn;
	private String cuser;
	private String device;
	private String doc_base; // html 파일 경로 /doc_base
	private String ia_filepath; //ia 파일 경로 (파일명 포함) default = ia_<code>.xlsx <code> 는 필수입력 code 이름
	private int req_pending_cnt; //작업요청후 대기중인 건수
	private int req_working_cnt; //작업요청후 작업중인건수
	private int req_complete_cnt; //작업요청후 작업완료된 건수
	
	@DateTimeFormat(pattern="yyyy-MM-ddTHH:mm:ss")
	private LocalDateTime regdate = LocalDateTime.now();
	
	private List<ChannelIaDTO> ia_tabs;
	
	@Getter
	@Setter
	@ToString
	public static class ChannelIaDTO {
		private String tab_name;
		private String ia_file;
	}
}
