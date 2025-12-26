package com.uixs.service.comment;

import java.util.List;

import com.uixs.model.comment.dto.CommentDTO;

public interface CommentService {
	public List<CommentDTO> listAll(String ref_id);
	
	public CommentDTO insert(CommentDTO commentDto) throws Exception;
	
	public int update(CommentDTO commentDto) throws Exception;
	
	public int deleteComment(String id) throws Exception;
}
