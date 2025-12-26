package com.uixs.model.fileupload.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.uixs.model.fileupload.dto.FileUploadDTO;

@Repository
public class FileUploadDAOImp implements FileUploadDAO {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION_NAME="upload_files"; //테이블 이름

	@Override
	public void saveAll(List<FileUploadDTO> files) {
		if (CollectionUtils.isEmpty(files)) {
			return;
		}
		
		try {
			for(FileUploadDTO fileDto : files) {
				mongoTemplate.insert(fileDto, COLLECTION_NAME);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 업로드 파일 조회
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table, String ref_table_key) {
		Query query = new Query(Criteria.where("ref_table").is(ref_table).and("ref_table_key").is(ref_table_key));
		
		return mongoTemplate.find(query, FileUploadDTO.class, COLLECTION_NAME);
	}
	
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table_key) {
		Query query = new Query(Criteria.where("_id").is(ref_table_key));
		
		return mongoTemplate.find(query, FileUploadDTO.class, COLLECTION_NAME);
	}
	
	@Override
	public FileUploadDTO selectUploadFileOne(String fileId) {
		Query query = new Query(Criteria.where("_id").is(new ObjectId(fileId)));
		
		return mongoTemplate.findOne(query, FileUploadDTO.class, COLLECTION_NAME);
	}
	
	@Override
	public void deleteFileInfo(String fileId) {
		Query query = new Query(Criteria.where("_id").is(fileId));
		
		mongoTemplate.remove(query, COLLECTION_NAME);
	}

	@Override
	public void deleteFileInfoWithRefKey(String ref_table_key) {
		// TODO Auto-generated method stub
		Query query = new Query(Criteria.where("ref_table_key").is(ref_table_key));
		
		mongoTemplate.remove(query, COLLECTION_NAME);
	}

}
