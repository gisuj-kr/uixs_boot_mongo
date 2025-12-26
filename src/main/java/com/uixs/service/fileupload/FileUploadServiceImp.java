package com.uixs.service.fileupload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.fileupload.dao.FileUploadDAO;
import com.uixs.model.fileupload.dto.FileUploadDTO;

@Service
public class FileUploadServiceImp implements FileUploadService {
	@Autowired
	FileUploadDAO fileUploadDao;

	@Override
	public void saveAll(List<FileUploadDTO> files) {
		fileUploadDao.saveAll(files);
	}
	
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table, String ref_id) {
		return fileUploadDao.selectUploadFile(ref_table, ref_id);
	}
	
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table_key) {
		return fileUploadDao.selectUploadFile(ref_table_key);
	}
	
	public FileUploadDTO selectUploadFileOne(String fileId) {
		return fileUploadDao.selectUploadFileOne(fileId);
	}
	
	@Override
	public void deleteFileInfo(String fileId) {
		fileUploadDao.deleteFileInfo(fileId);
	}

	@Override
	public void deleteFileInfoWithRefKey(String ref_table_key) {
		// TODO Auto-generated method stub
		fileUploadDao.deleteFileInfoWithRefKey(ref_table_key);
	}
}
