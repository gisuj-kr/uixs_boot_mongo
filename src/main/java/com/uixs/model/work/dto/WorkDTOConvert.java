package com.uixs.model.work.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.uixs.MongoDateStringDeserializer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
@Document(collection = "request_list")
public class WorkDTOConvert {
	@Id
	private String id;
	
	private String request_id; 								// 작업요청 아이디
	private String request_type;
	private String request_title; 						// 작업요청 제목
	private String request_content; 					// 작업요청 내용
	private String request_state; 							// 작업요청 상태 PENDING, WORKING, DELETE, CANCEL, COMPLETE
	private String cancel_content; 							// 작업요청 거부시 거부 사유
	private String site_code; 								// 사이트코드 (channel)
	private String site_name;								// 사이트 이름 (channel)
	private String state;
	private String username; 								// 작업완료 날짜
	private String userid; 									// 작성자 아이디
	private String search_key;								// 검색 조건
	private String search_word;								// 검색어
	private String working_part;							// 작업중인 파트
	private String requestor_name; 							// 작업요청자명
	
	private String start;										// 조회 시작 번호
	private String limit;	

	private List<String> need_workers;
	
	private WorkStateDTO plan_state;						// 기획 작업상태
	private WorkStateDTO publish_state;						// 퍼블리싱 작업상태
	private WorkStateDTO design_state;						// 디자인 작업상태
//	private Map<String, WorkStateDTO> work_state;
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String response_date; 					// 작업 수용날짜
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String complete_date; 					// 작업완료 날짜
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String end_date; 							// 작업요청시 입력한 완료 희망일자
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String request_date; 						// 업무요청일
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String request_complete_date; 				// 완료 요청일
	
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String regdate; 							// 작업요청일자
	
	private List<PartInfo> part;
	
	@Setter
	@Getter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PartInfo {
		private String name;	// 파트명
		private String worker;	// 담당자
		private String state; 	// PENDING=대기, WORKING=작업중, CONFIRM_REQUEST=컨펌요청, CONFIRM_COMPLETE=컨펌완료
		private String bigo;
		
		private List<WorkContent> work_content;
		
		@JsonDeserialize(using = MongoDateStringDeserializer.class)
		private String part_work_rday;  //업무 요청일
		
		@JsonDeserialize(using = MongoDateStringDeserializer.class)
		private String part_work_crday;  //완료 요청일
	}
	
	@Setter
	@Getter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class WorkContent {
		private String worker;
		private String content;
		
		@JsonDeserialize(using = MongoDateStringDeserializer.class)
		private String part_work_sday;  //작업 착수일
		
		@JsonDeserialize(using = MongoDateStringDeserializer.class)
		private String part_work_eday;  //작업 완료일
	}
}
