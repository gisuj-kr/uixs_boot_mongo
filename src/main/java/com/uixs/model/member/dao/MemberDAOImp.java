package com.uixs.model.member.dao;

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
import com.uixs.model.member.dto.MemberDTO;

@Repository("memberDao")
public class MemberDAOImp implements MemberDAO {

    private static final Logger logger = LoggerFactory.getLogger(MemberDAOImp.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper;

    public MemberDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // for LocalDateTime
    }

    private MemberDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, MemberDTO.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON to MemberDTO", e);
            return null;
        }
    }

    private String toJson(MemberDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write MemberDTO to JSON", e);
            return "{}";
        }
    }

	@Override
	public MemberDTO loginCheck(String userid, String passwd) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM member WHERE json_extract(doc, '$.userid') = ? AND json_extract(doc, '$.password') = ?", 
            String.class, userid, passwd);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}
	
	@Override
	public MemberDTO memberSelectOne(String userid) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM member WHERE json_extract(doc, '$.userid') = ?", 
            String.class, userid);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}
	
	@Override
	public List<MemberDTO> memberList(Long start, int limit) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM member WHERE json_extract(doc, '$.userid') != 'admin' ORDER BY json_extract(doc, '$.reg_date') DESC LIMIT ? OFFSET ?", 
            String.class, limit, start);
        List<MemberDTO> list = new ArrayList<>();
        for (String json : jsons) {
            MemberDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
	@Override
	public List<MemberDTO> memberWithPart(String part) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT doc FROM member WHERE json_extract(doc, '$.part') = ?", 
            String.class, part);
        List<MemberDTO> list = new ArrayList<>();
        for (String json : jsons) {
            MemberDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
	}
	
	@Override
	public MemberDTO memberInsert(MemberDTO user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        jdbcTemplate.update("INSERT INTO member (id, doc) VALUES (?, ?)", user.getId(), toJson(user));
        return user;
	}
	
	@Override
	public int memberUpdate(MemberDTO user) {
        MemberDTO existing = memberSelectOne(user.getUserid());
        if (existing == null) return 0;
        
        if (user.getUsername() != null) existing.setUsername(user.getUsername());
        if (user.getTeam() != null) existing.setTeam(user.getTeam());
        if (user.getPart() != null) existing.setPart(user.getPart());
        if (user.getTel() != null) existing.setTel(user.getTel());
        if (user.getEmail() != null) existing.setEmail(user.getEmail());
        if (user.getAuth() != null) existing.setAuth(user.getAuth());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existing.setPassword(user.getPassword());
        }
        
        return jdbcTemplate.update("UPDATE member SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
	@Override
	public int memberDelete(String id) {
		return jdbcTemplate.update("DELETE FROM member WHERE id = ?", id);
	}
	
	@Override
	public int memberTotalCount() {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM member", Integer.class);
        return count != null ? count : 0;
	}
	
	@Override
	public int selectTotUser() {
        return memberTotalCount();
	}
	
	@Override
	public void join(MemberDTO dto) {
		memberInsert(dto);
	}
}
