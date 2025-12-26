package com.uixs.service.work;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.work.dao.WorkDAO;
import com.uixs.model.work.dto.WorkDTO;

@Service
public class WorkServiceImp implements WorkService {
	
	@Autowired
	private WorkDAO workDao;

	
	@Override
	public WorkDTO insert(WorkDTO workDto) throws Exception {
		WorkDTO result = workDao.insert(workDto);
		
		return result;
	}
	
	/**
	 * 신규 아이디 저장을 위한 아이디 검색
	 * @param request_type
	 * @return
	 */
	@Override
	public String selectRequestId(String request_type) {
		String request_id = workDao.selectRequestId(request_type);
		
		return request_id;
	}
	
	/**
	 * 작업요청시 선택한 ia 항목저장
	 * @param requestIaParam
	 * @throws Exception
	 */
	@Override
	public void insertRequestIa(Map<String, Object> requestIaParam) throws Exception {
//		mapper.insertRequestIa(requestIaParam);
	}
	
	/**
	 * 작업요청 내역
	 * @return
	 */
	@Override
	public List<WorkDTO> selectRequestList(WorkDTO work) {
		return (List<WorkDTO>) workDao.selectRequestList(work);
	}
	
	@Override
	public List<WorkDTO> selectListWhereDate(String startDate, String endDate) {
		return (List<WorkDTO>) workDao.selectListWhereDate(startDate, endDate);
	}
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	@Override
	public WorkDTO selectRequestOne(String request_id) {
		return workDao.selectRequestOne(request_id);
	}
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	@Override
	public WorkDTO selectWorkOne(String request_id) {
		return workDao.selectWorkOne(request_id);
	}
	
	@Override
	public int modifyRequest(WorkDTO work) {
		return workDao.modifyRequest(work);
	}
	
	
	
	/**
	 * 요청 작업의 수락 혹은 거절 상태 저장
	 * @param workVo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateRequestWorkState(WorkDTO work) throws Exception {
		return workDao.updateRequestWorkState(work);
	}
	
	/**
	 * 작업진행내역
	 * @return
	 */
	@Override
	public List<WorkDTO> selectProcessList(WorkDTO work) {
		return workDao.selectProcessList(work);
	}
	
	/**
	 * 작업진행내역
	 * @return
	 */
	@Override
	public List<WorkDTO> selectCompleteList(String site_code) {
		return workDao.selectCompleteList(site_code);
	}
	
	/**
	 * 작업진행내역 페이징
	 * @return
	 */
	@Override
	public List<WorkDTO> selectCompleteList(WorkDTO work) {
		return workDao.selectCompleteList(work);
	}
	
	/**
	 * 작업진행 항목 저장
	 * @param request_id
	 * @param state
	 * @throws Exception
	 */
	@Override
	public void insertWorkList(String request_id, String state) throws Exception {
		workDao.insertWorkList(request_id, state);
		
//		return work.getWork_id();
	}
	
	/**
	 * 작업진행상태 저장
	 * @param workVo
	 * @throws Exception
	 */
	@Override
	public void insertWorkState(WorkDTO work) throws Exception {
		workDao.insertWorkState(work);
	}
	
	
	/**
	 * 작업진행상태 수정
	 * @param requestParam
	 * @throws Exception
	 */
	@Override
	public int updateWorkState(WorkDTO work) throws Exception {
		return workDao.updateWorkState(work);
	}
	
	/**
	 * 작업 검수요청 및 검수완료 건수
	 * @param requestParam
	 */
	@Override
	public int selectConfirmCount(String site_code){
		return workDao.selectConfirmCount(site_code);
	}
	
	// 작업상태저장 태이블에서 요청작업항목 제거
	@Override
	public int deleteWorkState(String id) throws Exception {
		return workDao.deleteWorkState(id);
	}
	
	// 작업목록에서 요청작업항목 제거
	@Override
	public int deleteWorkList(String request_id) throws Exception {
		return workDao.deleteWorkList(request_id);
	}
	
	// 달별 완료일 기준 작업요청 내역(완료일) - 조회
	@Override
	public List<Map<String, Object>> selectRequestWorkWithMonth(Map<String, Object> paramMap) {
		return workDao.selectRequestWorkWithMonth(paramMap);
	}

	@Override
	public int getWorkCntWithSiteCode(String site_code, String request_state, String searchKey, String searchWord) {
		// TODO Auto-generated method stub
		return workDao.getWorkCntWithSiteCode(site_code, request_state, searchKey, searchWord);
	}
	
	@Override
	public int updateData(WorkDTO dto) {
		return workDao.updateData(dto);
	}
	
	@Override
	public int removePartByName(String reqId, String partName) {
		return workDao.removePartByName(reqId,  partName);
	}
	
	/**
	 * 작업자별 작업내역 리스트
	 */
	@Override
	public List<WorkDTO> getWorkingRequestsAlternative() {
		return workDao.getWorkingRequestsAlternative();
	}
}
