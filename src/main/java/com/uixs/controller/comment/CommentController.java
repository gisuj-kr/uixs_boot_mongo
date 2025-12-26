package com.uixs.controller.comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.uixs.model.comment.dto.CommentDTO;
import com.uixs.model.fileupload.dto.FileUploadDTO;
import com.uixs.service.comment.CommentService;
import com.uixs.service.fileupload.FileUploadService;
import com.uixs.util.FileUpload;

@Controller
public class CommentController {
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	
	@Autowired
	private FileUpload fileUpload;
	
	@Autowired
	private FileUploadService fileUploadService;
	
	@Autowired
	private CommentService commentService;
	
	
	@RequestMapping(value = {"/comment/main"}, method = RequestMethod.GET)
	public String comment(Locale locale){
		logger.info("Welcome home! The client locale is {}.", locale);
		
		return "comment/work0100";
	}
	
	
	@RequestMapping(value = {"/comment/list.dat"}, method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> list(@RequestParam(value="ref_id") String ref_id) throws Exception{
		// 디비 select 코멘트 목록
		List<CommentDTO> dbList = commentService.listAll(ref_id);
		
		// 파일정보검색해서 새로생성한 코멘트 목록
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		
		try {
			if (dbList != null) {
				for (int i = 0; i < dbList.size(); i++) {
					CommentDTO comment = dbList.get(i);
					Map<String, Object> commentAndFiles = new HashMap<String, Object>();
					
					String id = comment.getId();
					
					List<FileUploadDTO> files = fileUploadService.selectUploadFile("comment", id);
					
					if (files != null) {
						commentAndFiles.put("files", files);
					}
					
					commentAndFiles.put("comment", comment);
					
					rtnList.add(commentAndFiles);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return rtnList;
	}
	
	@RequestMapping(value = {"/comment/insert.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public CommentDTO commentInsert(
			CommentDTO commentDto,
			@RequestParam(value="files", required=false) List<MultipartFile> mtFiles) throws Exception{

		CommentDTO insertDto = null;
		
		try {
			insertDto = commentService.insert(commentDto);
			
			// 파일 업로드
			if (mtFiles != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("ref_table", "comment");
				map.put("ref_table_key", insertDto.getId());
				
				if (insertDto.getId() != null) {
					List<FileUploadDTO> uploadFileList = fileUpload.uploadFiles(mtFiles, map);
					
					// 업로드파일 정보 저장
					fileUploadService.saveAll(uploadFileList);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return insertDto;
	}
	
	/**
	 * 댓글 수정
	 * @param commentVO
	 * @param mtfRequest
	 * @param request
	 * @param mpfReq
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"/comment/update.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> commentUpdate(
			CommentDTO commentDto,
			@RequestParam(value="files", required=false) List<MultipartFile> mtFiles) throws Exception{
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String ret = "";
		int updateCnt = 0;
		
		map.put("result", updateCnt);
	
		try {
			updateCnt = commentService.update(commentDto);
			
			if (updateCnt > 0 ) {
				map.put("ref_table", "comment");
				map.put("ref_table_key", commentDto.getId());
				
				List<FileUploadDTO> uploadFileList = fileUpload.uploadFiles(mtFiles, map);
				
				// 업로드파일 정보 저장
				fileUploadService.saveAll(uploadFileList);
			}
			
			map.put("updateCnt", updateCnt);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return map; //Integer.toString(updateCnt);
	}
	
	@RequestMapping(value = {"/comment/delete.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public String commentDeleteData(@RequestParam(value="id") String id) {
		logger.info("cmt_no::"+id);
		try {
			commentService.deleteComment(id);
			
//			if (delFiles.size() > 0) {
				// 파일 여러개 삭제
				fileUpload.deleteFiles(id);
//			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	/**
	 * 파일 한개 삭제
	 * @param file_id
	 * @return
	 */
	@RequestMapping(value= {"/comment/delete_file.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public String delete_file(@RequestParam(value="fileId") String fileId) {
		System.out.println("fileId::" + fileId);
		String result;
		
		try {
			fileUpload.deleteFile(fileId);
			
			result = "SUCCESS";
		}
		catch(Exception e) {
			result = e.getMessage();
			
			e.printStackTrace();
		}
		
		return result;
	}
}
