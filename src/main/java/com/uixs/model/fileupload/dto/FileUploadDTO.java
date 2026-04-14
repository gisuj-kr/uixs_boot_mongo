package com.uixs.model.fileupload.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileUploadDTO {
	
	private String id;					// 게시물 고유 아이디 (mongoId와  아이디와 동일)
	
	private String mongoId;				// 몽고디비 아이디
	private String ref_table;			// 관련 게시판
	private String ref_table_key;		// 관련 게시물 번호
	private String original_filename;	// 원본 파일명
	private String save_filename;		// 저장된 파일명
	private String upload_path;			// 파일 저장 경로
	private long size;					// 파일 용량
	
	public FileUploadDTO() {
	}

	@Builder
	public FileUploadDTO (
			String ref_table, 
			String ref_table_key, 
			String original_filename, 
			String save_filename,
			String upload_path,
			long size) {
		
		this.ref_table = ref_table;
		this.ref_table_key = ref_table_key;
		this.original_filename = original_filename;
		this.save_filename = save_filename;
		this.upload_path = upload_path;
		this.size = size;
	}
}
