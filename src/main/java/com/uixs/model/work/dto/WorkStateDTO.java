package com.uixs.model.work.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.uixs.MongoDateStringDeserializer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class WorkStateDTO {
	private String worker; // 담당자
	private String state; // PENDING=대기, WORKING=작업중, CONFIRM_REQUEST=컨펌요청, CONFIRM_COMPLETE=컨펌완료

	// @DateTimeFormat(pattern="yyyy-MM-dd")
	// private LocalDateTime work_sdate; //착수일
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String work_sdate; // 착수일

	// @DateTimeFormat(pattern="yyyy-MM-dd")
	// private LocalDateTime work_temp_edate; // 예상 완료일
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String work_temp_edate; // 예상 완료일

	// @DateTimeFormat(pattern="yyyy-MM-dd")
	// private LocalDateTime work_edate; //최종 완료일
	@JsonDeserialize(using = MongoDateStringDeserializer.class)
	private String work_edate; // 최종 완료일
}
