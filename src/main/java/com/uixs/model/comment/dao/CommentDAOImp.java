package com.uixs.model.comment.dao;

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
import com.uixs.model.comment.dto.CommentDTO;

@Repository("commentDao")
public class CommentDAOImp implements CommentDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(CommentDAOImp.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    private ObjectMapper objectMapper;

    public CommentDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private CommentDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, CommentDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to CommentDTO", e);
            return null;
        }
    }

    private String toJson(CommentDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write CommentDTO to JSON", e);
            return "{}";
        }
    }
	
	@Override
	public List<CommentDTO> listAll(String ref_id) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM comment WHERE json_extract(doc, '$.ref_id') = ?", String.class, ref_id);
        List<CommentDTO> list = new ArrayList<>();
        for (String json : jsons) {
            CommentDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
	@Override
	public CommentDTO insert(CommentDTO commentDto) throws Exception {
        if (commentDto.getId() == null || commentDto.getId().isEmpty()) {
            commentDto.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        jdbcTemplate.update("INSERT INTO comment (id, doc) VALUES (?, ?)", commentDto.getId(), toJson(commentDto));
        return commentDto;
	}
	
	@Override
	public int update(CommentDTO commentDto) throws Exception {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM comment WHERE id = ?", String.class, commentDto.getId());
        if (jsons.isEmpty()) return 0;
        
        CommentDTO existing = parseJson(jsons.get(0));
        if (existing == null) return 0;
        
        existing.setWriter_type(commentDto.getWriter_type());
        existing.setContent(commentDto.getContent());
        
        return jdbcTemplate.update("UPDATE comment SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
	@Override
	public int deleteComment(String id) throws Exception {
		return jdbcTemplate.update("DELETE FROM comment WHERE id = ?", id);
	}
}
