package com.uixs.model.work.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// SQLite 마이그레이션 이후 MongoDB의 $date 형식은 사용하지 않으므로
// MongoDateStringDeserializer 제거 및 @JsonIgnoreProperties 추가
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@ToString
public class WorkStateDTO {
	private String worker; // 담당자
	private String state; // PENDING=대기, WORKING=작업중, CONFIRM_REQUEST=컨펌요청, CONFIRM_COMPLETE=컨펌완료

	private String work_sdate; // 착수일 (yyyy-MM-dd 형식 문자열)

	private String work_temp_edate; // 예상 완료일 (yyyy-MM-dd 형식 문자열)

	private String work_edate; // 최종 완료일 (yyyy-MM-dd 형식 문자열)
}
