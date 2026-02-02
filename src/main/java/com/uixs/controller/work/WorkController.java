package com.uixs.controller.work;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.uixs.model.fileupload.dto.FileUploadDTO;
import com.uixs.model.work.dto.WorkDTO;
import com.uixs.service.fileupload.FileUploadService;
import com.uixs.service.work.WorkService;
import com.uixs.util.FileUpload;

@Controller
@RequestMapping("/work/*")
public class WorkController {
	private static final Logger logger = LoggerFactory.getLogger(WorkController.class);

	@Autowired
	private WorkService workService;

	@Autowired
	private FileUpload fileUpload;

	@Autowired
	private FileUploadService fileUploadService;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMddHHmmss",
	// timezone = "Asia/Seoul")
	// private LocalDateTime localDateTime = LocalDateTime.now();

	// @Autowired
	// private IaService iaService;

	/**
	 * 작업요청 view
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work/request_insert.do" })
	public String Request() {
		return "/work/work0200";
	}

	/**
	 * 작업관리 view
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work/list.do" })
	public String WorkList(Model model) {

		return "/work/work0100";
	}

	/**
	 * 작업관리 view
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work0300" })
	public String WorkList2() {
		return "/work/work0300";
	}

	@RequestMapping(value = { "/work/request/insert.view2" })
	public String insertView2() {
		return "/work/work0201";
	}

	@RequestMapping(value = { "/work/request/insert.data2" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insert2(
			HttpServletRequest request) throws IOException {

		MultipartHttpServletRequest mtfRequest = (MultipartHttpServletRequest) request;

		Map<String, Object> map = new HashMap<String, Object>();

		return map;
	}

	/**
	 * 작업요청항목 및 첨부파일 저장
	 * 
	 * @param workVo
	 * @param mtfRequest
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/work/request_insert.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insert(
			WorkDTO workDto,
			@RequestParam(value = "files", required = false) List<MultipartFile> mtFiles) throws IOException {

		System.out.println(workDto.toString());
		// return object
		Map<String, Object> map = new HashMap<String, Object>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Date now = new Date();
		// 오늘 날짜
		String nowDate = sdf.format(now);

		// 작업요청 구분 : NEW = 신규, EDIT = 수정
		String requestIdStartChar = "R";
		// try {
		// requestIdStartChar = "NEW".equals(workDto.getRequest_type()) ? "N" : "E";
		// }
		// catch (Exception e) {
		// requestIdStartChar = "N";
		// }
		//
		// // 신규 & 변경 삭제하면 새로운 아이디 Request 코드로 등록
		// requestIdStartChar = "R";

		// 저장된 작업요청 아이디 검색
		String request_id = workService.selectRequestId(workDto.getRequest_type());

		// 검색된 요청 아이디 없으면 초기값 00000
		if ("".equals(request_id) || request_id == null) {
			request_id = "00000";
		} else {
			request_id = request_id.substring(request_id.length() - 5, request_id.length());
		}

		// 검색된 작업요청 아이디 + 1
		int intRequestId = Integer.parseInt(request_id) + 1;

		DecimalFormat df = new DecimalFormat("00000");

		String newRequestId = nowDate + "-" + requestIdStartChar + df.format(intRequestId);

		// request_id 설정
		workDto.setRequest_id(newRequestId);
		workDto.setRequest_type("NEW");

		// 입력일자 설정
		workDto.setRegdate(LocalDateTime.now());

		try {
			// 작업요청 데이터 삽입
			WorkDTO savedWorkDto = workService.insert(workDto);

			// if (mtFiles != null) {
			// 파일 업로드
			// 파일업로드 추가정보 : 데이터베이스 저장용
			// Map<String, Object> uploadAddInfo = new HashMap<String, Object>();
			// 관련 태이블명
			// uploadAddInfo.put("ref_table", "request_list");
			// 관련 태이블 키값
			// uploadAddInfo.put("ref_table_key", savedWorkDto.getId());

			// 파일 업로드
			// List<FileUploadDTO> fileList = fileUpload.uploadFiles(mtFiles,
			// uploadAddInfo);

			// 업로드파일 정보 저장
			// fileUploadService.saveAll(fileList);
			// }

			map.put("REQUEST_ID", newRequestId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	// 요청작업 수정 함수
	@RequestMapping(value = { "/work/modify_request.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int modifyRequest(@RequestBody WorkDTO workDto) {
		try {
			// 작업요청 데이터 삽입
			return workService.modifyRequest(workDto);
		} catch (Exception e) {
			e.printStackTrace();

			return 0;
		}
	}

	/**
	 * 요청작업 목록 검색
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work/request_list.dat" })
	@ResponseBody
	public List<WorkDTO> workRequestList(@RequestBody WorkDTO workDto) {
		System.out.println("request_list");
		return workService.selectRequestList(workDto);
	}

	/**
	 * 진행중인 작업록록 검색
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work/work_ing_list.dat" }, method = RequestMethod.GET)
	@ResponseBody
	public List<WorkDTO> processWorkList(@RequestParam Map<String, String> params) {
		WorkDTO workDto = new WorkDTO();

		System.out.println("process_list");

		workDto.setStart(params.get("start"));
		workDto.setLimit(params.get("limit"));
		workDto.setSite_code(params.get("site_code"));

		if (null != params.get("search_key") && !params.get("search_key").isEmpty()) {
			workDto.setSearch_key(params.get("search_key"));
		}

		if (null != params.get("search_word") && !params.get("search_word").isEmpty()) {
			workDto.setSearch_word(params.get("search_word"));
		}

		return workService.selectProcessList(workDto);
	}

	/**
	 * 완료된 작업록록 검색
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/work/complete_list.dat" }, method = RequestMethod.GET)
	@ResponseBody
	public List<WorkDTO> completeWorkList(@RequestParam Map<String, String> params) {

		System.out.println("complete_list");

		WorkDTO workDto = new WorkDTO();

		workDto.setStart(params.get("start"));
		workDto.setLimit(params.get("limit"));
		workDto.setSite_code(params.get("site_code"));

		if (null != params.get("search_key") && !params.get("search_key").isEmpty()) {
			workDto.setSearch_key(params.get("search_key"));
		}

		if (null != params.get("search_word") && !params.get("search_word").isEmpty()) {
			workDto.setSearch_word(params.get("search_word"));
		}

		return workService.selectCompleteList(workDto);
	}

	/**
	 * 작업요청 내역 상세정보
	 * 
	 * @param ref_id : 몽고딛비 고유 아이디 _id 컬럼 값
	 * @return 작업요청내역 상세 + 요청작업 관련 업로드파일 목록
	 */
	@RequestMapping(value = { "/work/request_detail.dat" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> workRequestDetail(@RequestParam(value = "id") String id) {

		// 작업요청 관련 파일 검색
		List<FileUploadDTO> files = fileUploadService.selectUploadFile("request_list", id);

		// 작업요청 내용
		WorkDTO workDto = workService.selectRequestOne(id);

		Map<String, Object> rtn = new HashMap<String, Object>();

		// return map
		rtn.put("FILES", files);
		rtn.put("REQUEST_WORK", workDto);

		return rtn;
		// return null;
	}

	/**
	 * 작업진행내역 상세정보
	 * 
	 * @param id : unique id
	 * @return 작업요청내역 상세 + 요청작업 관련 업로드파일 목록
	 */
	@RequestMapping(value = { "/work/work_ing_detail.dat" }, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> workWorkOne(String id) {

		// 작업요청 관련 파일 검색
		List<FileUploadDTO> files = fileUploadService.selectUploadFile("request_list", id);
		// 작업요청 내용
		WorkDTO workDto = workService.selectRequestOne(id);

		Map<String, Object> rtn = new HashMap<String, Object>();

		// return map
		rtn.put("FILES", files);
		rtn.put("REQUEST_WORK", workDto);

		return rtn;
	}

	/**
	 * 요청작업 수용/거절 상태 수정
	 * 
	 * @param workVo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/work/request_change_state.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int changeState(@RequestBody WorkDTO workDto) {

		int rtn = 0;

		try {
			rtn = workService.updateRequestWorkState(workDto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtn;
	}

	/**
	 * 요청작업 삭제
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = { "/work/request_delete.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int workRequestDelete(@RequestBody WorkDTO param) {

		int delWorkList = 0;

		try {

			delWorkList = workService.deleteWorkState(param.getId());

			fileUpload.deleteFiles(param.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return delWorkList;
	}

	/**
	 * 작업 갯수 사이트별, 작업상태별(요청, 진행, 완료), 검색조건 별
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = { "/work/working_cnt.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int getWorkCntWithSiteCode(@RequestBody WorkDTO dto) {

		int result = 0;

		try {
			String searchKey = dto.getSearch_key();
			String searchWord = dto.getSearch_word();

			result = workService.getWorkCntWithSiteCode(
					dto.getSite_code(),
					dto.getRequest_state(),
					searchKey,
					searchWord);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 요청작업 검수요청 갯수/검수완료 갯수 검색
	 */
	@RequestMapping(value = { "/work/confirm_cnt.dat" })
	@ResponseBody
	public int confirmCnt(String site_code) {

		int rtn = 0;

		try {
			rtn = workService.selectConfirmCount(site_code);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtn;
	}

	// 달별 완료일 기준 작업요청 내역(완료일) - 조회
	@RequestMapping(value = { "/work/cal_data" }, method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> monthRequestWork(@RequestParam Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		resultList = workService.selectRequestWorkWithMonth(paramMap);

		return resultList;
	}

	@RequestMapping(value = { "/work/month_work_list.dat" }, method = RequestMethod.GET)
	@ResponseBody
	public List<WorkDTO> monthWorkList(
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) {

		List<WorkDTO> rtn = new ArrayList<WorkDTO>();

		try {
			rtn = workService.selectListWhereDate(startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtn;
	}

	// 달별 완료일 기준 작업요청 내역(완료일) - 조회
	@RequestMapping(value = { "/work/request_update.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int monthRequestWork(@RequestBody WorkDTO dto) {

		int res = 0;

		try {
			res = workService.updateData(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	// 특정 WorkDTO 문서의 part 배열에서 name이 partName 인 요소를 삭제합니다.
	@RequestMapping(value = { "/work/remove_part.dat" }, method = RequestMethod.POST)
	@ResponseBody
	public int removePartByName(String reqId, String partName) {
		int res = 0;

		try {
			res = workService.removePartByName(reqId, partName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 작업자별 작업내역 리스트
	 * 
	 * @param workDto
	 * @return
	 */
	@PostMapping("/work/user_working_list.dat")
	public ResponseEntity<List<WorkDTO>> getWorkingRequestsAlternative() {
		Map<String, Object> result = new HashMap<String, Object>();
		List<WorkDTO> list = new ArrayList<WorkDTO>();

		try {
			// 데이터 저장
			list = workService.getWorkingRequestsAlternative();

			return ResponseEntity.ok(list);

		} catch (Exception e) {
			// 상세 에러 로깅
			e.printStackTrace();
			System.out.println("Error details: " + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(list);
		}
	}

}
