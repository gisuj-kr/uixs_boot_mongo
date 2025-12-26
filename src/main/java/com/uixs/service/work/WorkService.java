package com.uixs.service.work;

import java.util.List;
import java.util.Map;


import com.uixs.model.work.dto.WorkDTO;

public interface WorkService {
	
	public WorkDTO insert(WorkDTO workDto) throws Exception;
	
	/**
	 * 신규 아이디 저장을 위한 아이디 검색
	 * @param request_type
	 * @return
	 */
	public String selectRequestId(String request_type);
	
	/**
	 * 작업요청시 선택한 ia 항목저장
	 * @param requestIaParam
	 * @throws Exception
	 */
	public void insertRequestIa(Map<String, Object> requestIaParam) throws Exception;
	
	/**
	 * 작업요청 내역
	 * @return
	 */
	public List<WorkDTO> selectRequestList(WorkDTO work);
	
	/**
	 * 오늘날짜 기준 해당 월의 시작일과 마지막일 사이에 착수한 작업 조회
	 * @return
	 */
	public List<WorkDTO> selectListWhereDate(String startDate, String endDate);
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	public WorkDTO selectRequestOne(String request_id);
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	public WorkDTO selectWorkOne(String request_id);
	
	/**
	 * 작업요청 수정
	 * @param work
	 * @return
	 * @throws Exception
	 */
	public int modifyRequest(WorkDTO work);
	
	/**
	 * 요청 작업의 수락 혹은 거절 상태 저장
	 * @param workVo
	 * @return
	 * @throws Exception
	 */
	public int updateRequestWorkState(WorkDTO work) throws Exception;
	/**
	 * 작업진행내역
	 * @return
	 */
	public List<WorkDTO> selectProcessList(WorkDTO work);
	
	/**
	 * 작업진행내역
	 * @return
	 */
	public List<WorkDTO> selectCompleteList(String site_code);
	/**
	 * 작업진행내역 페이징
	 * @return
	 */
	public List<WorkDTO> selectCompleteList(WorkDTO work);
	
	/**
	 * 작업진행 항목 저장
	 * @param request_id
	 * @param state
	 * @throws Exception
	 */
	public void insertWorkList(String request_id, String state) throws Exception;
	
	/**
	 * 작업진행상태 저장
	 * @param workVo
	 * @throws Exception
	 */
	public void insertWorkState(WorkDTO work) throws Exception;
	
	/**
	 * 작업진행상태 수정
	 * @param requestParam
	 * @throws Exception
	 */
	public int updateWorkState(WorkDTO work) throws Exception;
	
	/**
	 * 작업 검수요청 건수
	 * @param requestParam
	 */
	public int selectConfirmCount(String site_code);
	
	// 작업상태저장 태이블에서 요청작업항목 제거
	public int deleteWorkState(String id) throws Exception;
	
	// 작업목록에서 요청작업항목 제거
	public int deleteWorkList(String request_id) throws Exception;
	
	// 달별 완료일 기준 작업요청 내역(완료일) - 조회
	public List<Map<String, Object>> selectRequestWorkWithMonth(Map<String, Object> paramMap);
	
	public int getWorkCntWithSiteCode(String site_code, String request_state, String searchKey, String searchWord);
	
	public int updateData(WorkDTO dto);
	
	// part 배열에서 해당 이름의 파트 삭제
	public int removePartByName(String reqId, String partName);
	/**
	 * 작업자별 작업내역 리스트
	 */
	public List<WorkDTO> getWorkingRequestsAlternative();
}


