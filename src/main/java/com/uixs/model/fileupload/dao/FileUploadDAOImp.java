package com.uixs.model.fileupload.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uixs.model.fileupload.dto.FileUploadDTO;

@Repository("fileUploadDao")
public class FileUploadDAOImp implements FileUploadDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUploadDAOImp.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    private ObjectMapper objectMapper;

    public FileUploadDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private FileUploadDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, FileUploadDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to FileUploadDTO", e);
            return null;
        }
    }

    private String toJson(FileUploadDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write FileUploadDTO to JSON", e);
            return "{}";
        }
    }

	@Override
	public void saveAll(List<FileUploadDTO> files) {
		if (CollectionUtils.isEmpty(files)) {
			return;
		}
		
		try {
			for(FileUploadDTO fileDto : files) {
                if (fileDto.getId() == null || fileDto.getId().isEmpty()) {
                    fileDto.setId(UUID.randomUUID().toString().replace("-", ""));
                }
                jdbcTemplate.update("INSERT INTO file_info (id, doc) VALUES (?, ?)", fileDto.getId(), toJson(fileDto));
			}
		}
		catch(Exception e) {
			logger.error("Failed to saveAll", e);
		}
	}
	
	// 업로드 파일 조회
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table, String ref_table_key) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM file_info WHERE json_extract(doc, '$.ref_table') = ? AND json_extract(doc, '$.ref_table_key') = ?", 
            String.class, 
            ref_table, 
            ref_table_key
        );
        List<FileUploadDTO> list = new ArrayList<>();
        for (String json : jsons) {
            FileUploadDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
	@Override
	public List<FileUploadDTO> selectUploadFile (String ref_table_key) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM file_info WHERE json_extract(doc, '$.ref_table_key') = ?", 
            String.class, 
            ref_table_key
        );
        List<FileUploadDTO> list = new ArrayList<>();
        for (String json : jsons) {
            FileUploadDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
	@Override
	public FileUploadDTO selectUploadFileOne(String fileId) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM file_info WHERE id = ?", String.class, fileId);
        if (jsons.isEmpty()) return null;
        return parseJson(jsons.get(0));
	}
	
	@Override
	public void deleteFileInfo(String fileId) {
		jdbcTemplate.update("DELETE FROM file_info WHERE id = ?", fileId);
	}

	@Override
	public void deleteFileInfoWithRefKey(String ref_table_key) {
		jdbcTemplate.update("DELETE FROM file_info WHERE json_extract(doc, '$.ref_table_key') = ?", ref_table_key);
	}

}
