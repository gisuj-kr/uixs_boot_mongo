package com.uixs.model.comment.dao;

import java.util.List;
import java.util.Map;

import com.uixs.model.comment.dto.CommentDTO;


public interface CommentDAO {
	public List<CommentDTO> listAll(String ref_id);
	
	public CommentDTO insert(CommentDTO commentDto) throws Exception;
	
	public int update(CommentDTO commentDto) throws Exception;
	
	public int deleteComment(String id) throws Exception;
}
