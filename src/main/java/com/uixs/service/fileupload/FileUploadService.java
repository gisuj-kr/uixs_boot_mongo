package com.uixs.service.fileupload;

import java.util.List;

import com.uixs.model.fileupload.dto.FileUploadDTO;

public interface FileUploadService {
	void saveAll(List<FileUploadDTO> files);
	
	public List<FileUploadDTO> selectUploadFile (String ref_table, String ref_id);
	
	public List<FileUploadDTO> selectUploadFile (String ref_table_key);
	
	public FileUploadDTO selectUploadFileOne(String fileId);
	
	public void deleteFileInfo(String fileId);
	
	public void deleteFileInfoWithRefKey(String ref_table_key);

}
