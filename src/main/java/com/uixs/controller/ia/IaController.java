package com.uixs.controller.ia;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uixs.model.ia.dto.IaDTO;
import com.uixs.service.ia.IaService;
import com.uixs.util.FileUpload;


@Controller
@RequestMapping("/ia/*")
public class IaController {
	private static final Logger logger = LoggerFactory.getLogger(IaController.class);
	
	@Autowired
	private IaService iaService;
	
	@Autowired
	private FileUpload fileUpload;
	
	@RequestMapping(value = {"/ia/list.do"})
	public String listView(Model model) {
		HttpServletRequest request =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		
		model.addAttribute("rpath", rootPath);
		
		return "/ia/ia0100";
	}
	
	@RequestMapping(value = {"/ia/manage"})
	public String manage() {
		return "menu/menu0200";
	}
	
	@RequestMapping(value = {"/ia/cal"})
	public String publist() {
		return "menu/menu0100";
	}
	
	@RequestMapping(value = {"/ia/html_list.do"})
	public String htmlListV() {
		return "ia/ia0200";
	}
	
	@RequestMapping(value = {"/ia/list.dat"}, method=RequestMethod.GET)
	@ResponseBody
	public List<IaDTO> iaListData(String mode, String site_code, String parent) {
		System.out.println("mode::"+mode+", site_code::"+ site_code + ", parent:"+parent);
		
		
		List<IaDTO> iaTree = iaService.getIa_tree(mode, site_code, parent, new ArrayList<IaDTO>());
		
		return iaTree;
	}
	
	/**
	 * 화면목록 데이터 조회
	 * @param iaVo
	 * @return
	 */
	@RequestMapping(value = {"/ia/html_list.dat"})
	@ResponseBody
	public List<IaDTO> htmlListD(String site_code) {
		
		List<IaDTO> datas = new ArrayList<IaDTO>();
		
		System.out.println("site_code::"+site_code);
		
		try {
			datas = iaService.getIa_tree(null, site_code, null, new ArrayList<IaDTO>());
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.info("datas:" + datas);
		
		return datas;
	}
	
	/**
	 * ia 내용 상세조회
	 * @param iaVo
	 * @return IaVO
	 */
	@RequestMapping(value = {"/ia/detail.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public IaDTO iaDetail(String id) {
		IaDTO data = new IaDTO();
		
		try {
			data = iaService.selectIaOne(id);//new ArrayList<IaVO>();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}	
	
	/**
	 * ia 정보 입력 - 이름 뎁스 등
	 * @param iaDto
	 * @return
	 */
	@RequestMapping(value = {"/ia/insert.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public IaDTO insertIaData(@RequestBody IaDTO iaDto) {
		int sort = 0;
		try {
			sort = iaService.selectMaxSort(iaDto);
			
			iaDto.setSort(sort+1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		IaDTO ret = iaService.insertIa(iaDto);
		
		return ret;
	}
	
	/**
	 * ia 리스트 수정 - ia 이름, 순서 수정
	 * @param iaDto
	 * @return
	 */
	@RequestMapping(value = {"/ia/update.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public int update(@RequestBody IaDTO iaDto) {
		
		int ret = 0;
		
		try {
			ret = iaService.updateIa(iaDto);
			
			if (iaDto.getSort_list() != null ) {
				for (IaDTO.SortItem item : iaDto.getSort_list()) {
					
					iaService.updateIaSort(item.getId(), item.getSort());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * ia 리스트 삭제
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = {"/ia/delete.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public int delete(@RequestBody IaDTO dto) {
		
		int ret = 0;
		
		try {
			ret = iaService.deleteIa(dto.getId());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * ia 정보 수정 - 상세정보 html 파일명, 링크, 최종 완료 상태 , 최종 완료일 수정
	 * @param iaDto
	 * @return
	 */
	@RequestMapping(value = {"/ia/update_state.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public int updateState(@RequestBody IaDTO iaDto) {
		System.out.println("updateState::" + iaDto);
		int updateCnt = 0;
		
		try {
			updateCnt = iaService.updateIaState(iaDto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return updateCnt;
	}
	
//	@RequestMapping(value = {"/ia/files"}, method = RequestMethod.POST)
//	@ResponseBody
//	public List<FileUploadVO> iaFiles(@RequestParam Map<String, Object> param) {
//		
//		String id = (String) param.get("id");
//		List<FileUploadVO> ret = new ArrayList<FileUploadVO>();
//		
//		try {
//			String[] ref_tables = {"IA_HTML", "IA_PPT"};
//		
//			
//			ret = fileUpload.selectFileInfoWithRefs(ref_tables, id);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return ret;
//	}
	
	/**
	 * ia tree 에서 html 파일 업로드
	 * */
//	@RequestMapping(value= {"/ia/uploadfile"}, method = RequestMethod.POST)
//	@ResponseBody
//	public Map<String, Object> uploadfile(
//			MultipartHttpServletRequest mtfRequest, 
//			HttpServletRequest request) {
//		
//		// return object
//		Map<String, Object> map = new HashMap<String, Object>();
//		
//		try {
//			String id = request.getParameter("id"); 
//			String siteCode = request.getParameter("site_code");
//			// 첨부된파일 아이디 : 기 첨부된파일 삭제를 위해
//			String[] file_id = request.getParameterValues("file_id");
//			
//			// 파일 구분 Default = 연관된 태이블명
//			String ref_table = "IA_TREE";
//			logger.info("file_ids"+file_id);
//			System.out.close();
//			
//			if (file_id != null) {
//				List<Object> delFileInfoList = new ArrayList<Object>();
//				
//				for(int i = 0; i < file_id.length; i++) {
//					Map<String, Object> delFileInfo = new HashMap<String, Object>();
//					
//					if (!"".equals(file_id[i])) {
//						delFileInfo.put("file_id", file_id[i]);
//						delFileInfoList.add(delFileInfo);
//					}
//					
//				}
//				
//				if (delFileInfoList.size() > 0) {
//					fileUpload.deleteFiles(delFileInfoList);
//				}
//			}
//			
//			logger.info("iaId:" + id);
//			// 파일 업로드
//			// 파일업로드 추가정보 : 데이터베이스 저장용
//			Map<String, Object> uploadAddInfo = new HashMap<String, Object>();
//			
//			// 관련 태이블명
//			uploadAddInfo.put("rel_table", ref_table);
//			// 관련 태이블 키값
//			uploadAddInfo.put("rel_table_key", id);
//			// 업로드 경로
//			uploadAddInfo.put("path", "/resources/"+siteCode);
//			
//			// 업로드 파일 정보 list
//			fileUpload.upload(mtfRequest, request, uploadAddInfo);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return map;
//	}
	
	/**
	 * ia tree 에서 html 파일 삭제
	 * */
//	@RequestMapping(value= {"/ia/delete_iafile"}, method = RequestMethod.POST)
//	@ResponseBody
//	public String delete_iafile(String file_id) {
//		Map<String, Object> delFileInfo = new HashMap<String, Object>();
//		List<Object> delFileInfoList = new ArrayList<Object>();
//		
//		String result = "";
//		try {
//			delFileInfo.put("file_id", file_id);
//			
//			delFileInfoList.add(delFileInfo);
//			
//			fileUpload.deleteFiles(delFileInfoList);
//			
//			result = "SUCCESS";
//		}
//		catch(Exception e) {
//			result = e.getMessage();
//			
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
	
	/**
	 * 메뉴에 요청된 작업 목록
	 * @param param
	 * @return
	 */
//	@RequestMapping(value= {"/ia/request_work_list"})
//	@ResponseBody
//	public List<Map<String, Object>> selectRequestWorkForIaFromDB(@RequestParam Map<String, String> param) {
//		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
//		
//		try {
//			ret = service.selectRequestWorkForIaFromDB(param);
//			
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return ret;
//	}

}
