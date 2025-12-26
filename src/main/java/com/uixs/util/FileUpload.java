package com.uixs.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uixs.model.fileupload.dto.FileUploadDTO;
import com.uixs.service.fileupload.FileUploadService;


@Component
public class FileUpload {

	private static final Logger logger = LoggerFactory.getLogger(FileUpload.class);
	
	@Value("${uixs.upload.path}") 
    private String uploadPath;
	
	@Autowired
	private FileUploadService uploadService;

	public List<FileUploadDTO> uploadFiles(final List<MultipartFile> multipartFiles, Map<String, Object> uploadAddInfo) {
        List<FileUploadDTO> files = new ArrayList<>();
        
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile.isEmpty()) {
                continue;
            }
            files.add(uploadFile(multipartFile, uploadAddInfo));
        }
        return files;
    }
	
	public FileUploadDTO uploadFile(final MultipartFile multipartFile, Map<String, Object> ref) {

		if (multipartFile.isEmpty()) {
            return null;
        }

        String saveName = generateSaveFilename(multipartFile.getOriginalFilename());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")).toString();
        String uploadPath = getUploadPath(today) + File.separator + saveName;
        String ref_table = (String) ref.get("ref_table");
        String ref_table_key = (String) ref.get("ref_table_key");
        
        File uploadFile = new File(uploadPath);

        try {
            multipartFile.transferTo(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return FileUploadDTO.builder()
        		.ref_table(ref_table)
        		.ref_table_key(ref_table_key)
    			.original_filename(multipartFile.getOriginalFilename())
    			.save_filename(saveName)
    			.upload_path(uploadPath)
    			.size(multipartFile.getSize())
                .build();

	}
	 
	private String generateSaveFilename(final String filename) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }

    /**
     * 업로드 경로 반환
     * @return 업로드 경로
     */
	private String getUploadPath() {
        return makeDirectories(uploadPath);
    }

    /**
     * 업로드 경로 반환
     * @param addPath - 추가 경로
     * @return 업로드 경로
     */
    private String getUploadPath(final String addPath) {
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    /**
     * 업로드 폴더(디렉터리) 생성
     * @param path - 업로드 경로
     * @return 업로드 경로
     */
	private String makeDirectories(final String path) {
        File dir = new File(path);
        if (dir.exists() == false) {
            dir.mkdirs();
        }
        return dir.getPath();
    }
	
	/**
	 * 게시물 아이디로 파일 전체 삭제하기
	 * @param ref_table_key
	 */
	public void deleteFiles(String ref_table_key) {
		if (ref_table_key != null) {
		    
			try {
				// 디비에서 파일정보 검색
				List<FileUploadDTO> savedFileInfo = uploadService.selectUploadFile(ref_table_key);
				
				if (savedFileInfo.size() > 0) {
					for(int i = 0; i < savedFileInfo.size(); i++) {
						
						FileUploadDTO fileInfo = savedFileInfo.get(i);
						
						String fileId = fileInfo.getId();
						String attachPath = fileInfo.getUpload_path();
						
						// 삭제될 파일 경로
						File file = new File(attachPath);
						
						// 실제 파일 삭제
						if (file.exists()) {
							file.delete();
						}
						// 디비에서 파일 정보 제거
						uploadService.deleteFileInfo(fileId);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 파일 아이디로 파일 하나만 삭제
	 * @param fileId
	 */
	public void deleteFile(String fileId) {
		try {
			FileUploadDTO fileInfo = uploadService.selectUploadFileOne(fileId);
			
			if (fileInfo != null) {
				String attachPath = fileInfo.getUpload_path();
				
				// 삭제될 파일 경로
				File file = new File(attachPath);
				
				// 실제 파일 삭제
				if (file.exists()) {
					file.delete();
				}
				// 디비에서 파일 정보 제거
				uploadService.deleteFileInfo(fileId);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
