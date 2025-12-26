package com.uixs.service.comment;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.comment.dao.CommentDAO;
import com.uixs.model.comment.dto.CommentDTO;

@Service
public class CommentServiceImp implements CommentService{
	
	@Autowired
	private CommentDAO commentDao;

	@Override
	public List<CommentDTO> listAll(String ref_id) {
		// TODO Auto-generated method stub
		return commentDao.listAll(ref_id);
	}

	@Override
	public CommentDTO insert(CommentDTO commentDto) throws Exception {
		// TODO Auto-generated method stub
		return commentDao.insert(commentDto);
	}

	@Override
	public int update(CommentDTO commentDto) throws Exception {
		// TODO Auto-generated method stub
		return commentDao.update(commentDto);
	}

	@Override
	public int deleteComment(String id) throws Exception {
		// TODO Auto-generated method stub
		return commentDao.deleteComment(id);
	}

}
