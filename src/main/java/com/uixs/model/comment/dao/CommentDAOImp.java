package com.uixs.model.comment.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.uixs.model.comment.dto.CommentDTO;

@Repository
public class CommentDAOImp implements CommentDAO {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION_NAME="comment"; //테이블 이름
	
	@Override
	public List<CommentDTO> listAll(String ref_id) {
		Query query = new Query(Criteria.where("ref_id").is(ref_id));
		
		return mongoTemplate.find(query, CommentDTO.class, COLLECTION_NAME);
	}
	
	@Override
	public CommentDTO insert(CommentDTO commentDto) throws Exception {
		return mongoTemplate.insert(commentDto, COLLECTION_NAME);
	}
	
	@Override
	public int update(CommentDTO commentDto) throws Exception {
		Query query = new Query(Criteria.where("_id").is(commentDto.getId()));
		
		Update update = new Update();
		update.set("writer_type", commentDto.getWriter_type());
		update.set("content", commentDto.getContent());
		
		return (int) mongoTemplate.updateFirst(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	@Override
	public int deleteComment(String id) throws Exception {
		Query query = new Query(Criteria.where("_id").is(id));
		
		return (int) mongoTemplate.remove(query, CommentDTO.class, COLLECTION_NAME).getDeletedCount();
	}
}
