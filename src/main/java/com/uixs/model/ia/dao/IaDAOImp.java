package com.uixs.model.ia.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uixs.model.ia.dto.IaDTO;

@Repository("iaDao")
public class IaDAOImp implements IaDAO {
	
    private static final Logger logger = LoggerFactory.getLogger(IaDAOImp.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper;

    public IaDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private IaDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, IaDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to IaDTO", e);
            return null;
        }
    }

    private String toJson(IaDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write IaDTO to JSON", e);
            return "{}";
        }
    }

	@Override
	public List<IaDTO> getIa_tree(String mode, String site_code, String parent) {
        StringBuilder sql = new StringBuilder("SELECT doc FROM ia WHERE json_extract(doc, '$.site_code') = ?");
        List<Object> params = new ArrayList<>();
        params.add(site_code);
        
        if (mode != null && "parent_only".equals(mode)) {
            sql.append(" AND json_extract(doc, '$.parent') = '#'");
        }
        
        if (parent != null && !parent.isEmpty()) {
            sql.append(" AND json_extract(doc, '$.parent') = ?");
            params.add(parent);
        }
        
        sql.append(" ORDER BY CAST(json_extract(doc, '$.sort') AS INTEGER) ASC");
        
        List<String> jsons = jdbcTemplate.queryForList(sql.toString(), String.class, params.toArray());
        List<IaDTO> children = new ArrayList<>();
        for (String json : jsons) {
            IaDTO dto = parseJson(json);
            if (dto != null) children.add(dto);
        }
        
        return children;
    }
	
	@Override
	public int selectMaxSort(IaDTO iaDto) {
        String sql = "SELECT doc FROM ia WHERE json_extract(doc, '$.parent') = ? AND json_extract(doc, '$.site_code') = ? ORDER BY CAST(json_extract(doc, '$.sort') AS INTEGER) DESC LIMIT 1";
        List<String> jsons = jdbcTemplate.queryForList(sql, String.class, iaDto.getParent(), iaDto.getSite_code());
		if (jsons.isEmpty()) return 0;
		IaDTO result = parseJson(jsons.get(0));
		return result != null ? result.getSort() : 0;
	}

	@Override
	public IaDTO insertIa(IaDTO iaDto) {
        if (iaDto.getId() == null || iaDto.getId().isEmpty()) {
            iaDto.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        jdbcTemplate.update("INSERT INTO ia (id, doc) VALUES (?, ?)", iaDto.getId(), toJson(iaDto));
		return iaDto;
	}

	@Override
	public IaDTO selectIaOne(String id) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM ia WHERE id = ?", String.class, id);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}

	@Override
	public int updateIa(IaDTO iaDto) {
		IaDTO existing = selectIaOne(iaDto.getId());
		if (existing == null) return 0;
		
		if (iaDto.getParent() != null) existing.setParent(iaDto.getParent());
		if (iaDto.getText() != null) existing.setText(iaDto.getText());
		if (iaDto.getLink() != null) existing.setLink(iaDto.getLink());
		if (iaDto.getConfirm_state() != null) existing.setConfirm_state(iaDto.getConfirm_state());
		if (iaDto.getPublish_state() != null) existing.setPublish_state(iaDto.getPublish_state());
		existing.setUpdate_date(LocalDateTime.now());
		
        return jdbcTemplate.update("UPDATE ia SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}

	@Override
	public int deleteIa(String id) {
		return jdbcTemplate.update("DELETE FROM ia WHERE id = ?", id);
	}

	@Override
	public int updateIaSort(String id, int sort) {
		IaDTO existing = selectIaOne(id);
		if (existing == null) return 0;
		existing.setSort(sort);
		return jdbcTemplate.update("UPDATE ia SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}

	@Override
	public int updateIaState(IaDTO dto) {
		IaDTO existing = selectIaOne(dto.getId());
		if (existing == null) return 0;
		
		existing.setText(dto.getText());
		existing.setLink(dto.getLink());
		existing.setPublish_state(dto.getPublish_state());
		existing.setConfirm_state(dto.getConfirm_state());
		existing.setUpdate_date(dto.getUpdate_date());
		
		return jdbcTemplate.update("UPDATE ia SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
}
