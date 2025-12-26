package com.uixs.model.channel.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.uixs.model.channel.dto.ChannelDTO;
import com.uixs.model.work.dto.WorkDTO;
import com.uixs.repository.channel.ChannelRepository;

@Repository
public class ChannelDAOImp implements ChannelDAO {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	ChannelRepository channelRepository;
	
	String COLLECTION_NAME="channel"; //테이블 이름
	
	public List<ChannelDTO> channels() {
		List<ChannelDTO> list = mongoTemplate.findAll(ChannelDTO.class, COLLECTION_NAME);
		
		if(list.size() > 0) {
			return list;
		} else {
			return null;
		}
	};
	
	// 채널 insert
	public void insertChannel(ChannelDTO dto) throws Exception {
		mongoTemplate.insert(dto, COLLECTION_NAME);
	};
	
	// 채널 update
	public int updateChannel(ChannelDTO dto) throws Exception {
		Query query = new Query(Criteria.where("_id").is(dto.getId()));
		
		Update update = new Update();
		
		update.set("code", dto.getCode());
		update.set("name", dto.getName());
		update.set("cuser", dto.getCuser());
		update.set("doc_base", dto.getDoc_base());
		update.set("ia_filepath", dto.getIa_filepath());
		update.set("ia_tabs", dto.getIa_tabs());
		
		long updatedCnt = mongoTemplate.updateFirst(query, update, ChannelDTO.class).getMatchedCount();
		
		return Long.valueOf(updatedCnt).intValue();
	};
	
	// 채널 하나 select
	public ChannelDTO selectChannelOne(String id) {
		
		System.out.println("id=="+id);
		Query query = new Query(new Criteria("_id").is(id));
		
		ChannelDTO dto = mongoTemplate.findOne(query, ChannelDTO.class, COLLECTION_NAME);
		
		return dto;
	};
	
	// 채널 하나 select
	public ChannelDTO selectCodeOne(String code) {
		
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("code").is(code)),
				Aggregation.limit(1)
		);
		
		AggregationResults<ChannelDTO> agResult = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, ChannelDTO.class);
		
		ChannelDTO dto = agResult.getUniqueMappedResult();
		
		return dto;
	};
	
	public int removeChannel(ChannelDTO dto) throws Exception{
		Query query = new Query(Criteria.where("_id").is(dto.getId()));
		
		long removeCnt = mongoTemplate.remove(query, ChannelDTO.class, COLLECTION_NAME).getDeletedCount();
		
		return (int) removeCnt;//Long.valueOf(removeCnt).intValue();
	}
	
}
