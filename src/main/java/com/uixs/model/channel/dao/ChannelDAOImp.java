package com.uixs.model.channel.dao;

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
import com.uixs.model.channel.dto.ChannelDTO;

@Repository("channelDao")
public class ChannelDAOImp implements ChannelDAO {
	
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImp.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper;

    public ChannelDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private ChannelDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, ChannelDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to ChannelDTO", e);
            return null;
        }
    }

    private String toJson(ChannelDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write ChannelDTO to JSON", e);
            return "{}";
        }
    }

    @Override
	public List<ChannelDTO> channels() {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM channel ORDER BY json_extract(doc, '$.regdate') DESC", String.class);
        List<ChannelDTO> list = new ArrayList<>();
        for (String json : jsons) {
            ChannelDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
    @Override
	public void insertChannel(ChannelDTO dto) throws Exception {
        if (dto.getId() == null || dto.getId().isEmpty()) {
            dto.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        jdbcTemplate.update("INSERT INTO channel (id, doc) VALUES (?, ?)", dto.getId(), toJson(dto));
	}
	
    @Override
	public int updateChannel(ChannelDTO dto) throws Exception {
        ChannelDTO existing = selectChannelOne(dto.getId());
        if (existing == null) return 0;

		existing.setCode(dto.getCode());
		existing.setName(dto.getName());
		existing.setCuser(dto.getCuser());
		existing.setDoc_base(dto.getDoc_base());
		existing.setIa_filepath(dto.getIa_filepath());
		existing.setIa_tabs(dto.getIa_tabs());
		
        return jdbcTemplate.update("UPDATE channel SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
    @Override
	public ChannelDTO selectChannelOne(String id) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM channel WHERE id = ?", String.class, id);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}
	
    @Override
	public ChannelDTO selectCodeOne(String code) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM channel WHERE json_extract(doc, '$.code') = ?", String.class, code);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}
	
    @Override
	public int removeChannel(ChannelDTO dto) throws Exception {
		return jdbcTemplate.update("DELETE FROM channel WHERE id = ?", dto.getId());
	}
}
