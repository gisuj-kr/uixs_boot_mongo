package com.uixs.model.ia.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class IaDTO {
	@Id
	private String id;
	private String parent;
	private String site_code;
	private String view_name;
	private String view_id;
	
	private String mode;
	private String name;
	private String text;
	private String link;
	
	private String path; // 화면경로 (DEPTH) ex) 조회 > 다른이름조회
	private String confirm_state; // 검수상태 1: 작업중, 2: 검수요청, 3: 검수완료
	private String publish_state; // 퍼블 상태 1: 작업중, 2: 작업완료, 3: 수정중
	
	
	private int sort;
	private int depth;
	private int file_cnt;
	
	private List<SortItem> sort_list;
	
	@DateTimeFormat(pattern="yyyy-MM-ddTHH:mm:ss")
	private LocalDateTime reg_date = LocalDateTime.now();
	
	@DateTimeFormat(pattern="yyyy-MM-ddTHH:mm:ss")
	private LocalDateTime update_date;
	
	private String work_request_cnt; // 해당 메뉴에 요청한 작업 갯수
	
	private List<IaDTO> ia_tree;	//
	
	public void setIa_tree(List<IaDTO> ia_tree) {
        this.ia_tree = ia_tree;
    }

	public static class SortItem {
	    private String id;
	    private int sort;
	    
	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public int getSort() {
	        return sort;
	    }

	    public void setSort(int sort) {
	        this.sort = sort;
	    }

		@Override
		public String toString() {
			return "SortItem [id=" + id + ", sort=" + sort + "]";
		}
	}
}


