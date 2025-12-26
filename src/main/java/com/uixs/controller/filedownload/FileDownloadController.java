package com.uixs.controller.filedownload;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import com.uixs.model.fileupload.dto.FileUploadDTO;
import com.uixs.service.fileupload.FileUploadService;

@Controller
public class FileDownloadController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileDownloadController.class);
	
	@Autowired
	private FileUploadService uploadService;
	
	
//	@RequestMapping(value = {"/file/download"}, method = RequestMethod.GET)
//	public ModelAndView fileDownload(
//			@RequestParam(value = "file_id") String file_id, 
//			HttpServletRequest request) {
//
//		
//		FileUploadDTO fileUploadDto = uploadService.selectUploadFileOne(file_id);
//		
//		String attachPath = fileUploadDto.getUpload_path() == null ? "" : fileUploadDto.getUpload_path();
//		String originalFileName = fileUploadDto.getOriginal_filename();
//		
//		File downloadFile = new File(attachPath);
//		
//		ModelAndView mv = new ModelAndView();
//		
//		mv.setViewName("fileDownloadView");
//		mv.addObject("downloadFile", downloadFile);
//		mv.addObject("originalFileName", originalFileName);
//		
//		return mv;
//	}
	
	@RequestMapping(value = {"/file/download"}, method = RequestMethod.GET)
	public ResponseEntity<UrlResource> download(@RequestParam(value = "file_id") String file_id) throws MalformedURLException {

		FileUploadDTO fileUploadDto = uploadService.selectUploadFileOne(file_id);

		UrlResource resource;
	    try{
	        resource = new UrlResource("file:"+ fileUploadDto.getUpload_path());
	    }catch (MalformedURLException e){
	        logger.error("the given File path is not valid");
	        e.getStackTrace();
	        throw new RuntimeException("the given URL path is not valid");
	    }
	    //Header
	    String originalFileName = fileUploadDto.getOriginal_filename();
	    String encodedOriginalFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);

	    String contentDisposition = "attachment; filename=\"" + encodedOriginalFileName + "\"";

	    return ResponseEntity
	            .ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition)
	            .body(resource);
    }
}
