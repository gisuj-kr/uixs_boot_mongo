package com.uixs.model.ia.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.uixs.model.ia.dto.IaDTO;

@Repository
public class IaDAOImp implements IaDAO {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	private final String COLLECTION_NAME="ia"; //테이블 이름
	
	
	@SuppressWarnings("null")
	@Override
	public List<IaDTO> getIa_tree(String mode, String site_code, String parent) {
		
		// 자식 노드들을 찾는 쿼리
        Criteria criteria = Criteria.where("site_code").is(site_code);
        
        if (mode != null && "parent_only".equals(mode)) {
        	criteria = new Criteria().andOperator(
        			criteria,
        			Criteria.where("parent").is("#")
        	);
        }
        
        if(parent != null && !parent.isEmpty()) {
        	criteria = new Criteria().andOperator(
        			criteria,
        			Criteria.where("parent").is(parent)
        	);
        }
        
        Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.ASC, "sort")
		);
        
        AggregationResults<IaDTO> result = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, IaDTO.class);
        
        List<IaDTO> children = result.getMappedResults();
        
        return children;
        
    }
	
	@Override
	public int selectMaxSort(IaDTO iaDto) {
		Query query = new Query();
		
		Criteria criteria = new Criteria();
		
		criteria.andOperator(
			Criteria.where("parent").is(iaDto.getParent()),
			Criteria.where("site_code").is(iaDto.getSite_code())
		);
		
		query.addCriteria(criteria);
		query.with(Sort.by(Sort.Direction.DESC, "sort"));
		query.limit(1);
		
		IaDTO result = mongoTemplate.findOne(query, IaDTO.class, COLLECTION_NAME);
		
		if (result == null) {
			return 0;
		}
		else {
			return result.getSort();
		}
	}

	@Override
	public IaDTO insertIa(IaDTO iaDto) {
		// TODO Auto-generated method stub
		return mongoTemplate.insert(iaDto, COLLECTION_NAME);
	}

	@Override
	public IaDTO selectIaOne(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		
		// TODO Auto-generated method stub
		return mongoTemplate.findOne(query, IaDTO.class, COLLECTION_NAME);
	}

	@Override
	public int updateIa(IaDTO iaDto) {
		
		Query query = new Query(Criteria.where("_id").is(iaDto.getId()));
		
		Update update = new Update();
		if (iaDto.getParent() != null) {
			update.set("parent", iaDto.getParent());
		}
		
		if (iaDto.getText() != null) {
			update.set("text", iaDto.getText());
		}
		
		if (iaDto.getLink() != null) {
			update.set("link", iaDto.getLink());
		}
		
		if (iaDto.getConfirm_state() != null) {
			update.set("confirm_state", iaDto.getConfirm_state());
		}
		
		if (iaDto.getPublish_state() != null) {
			update.set("publish_state", iaDto.getPublish_state());
		}
		
		update.set("update_date", LocalDateTime.now());
		
		
		return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}

	@Override
	public int deleteIa(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		// TODO Auto-generated method stub
		return (int) mongoTemplate.remove(query, COLLECTION_NAME).getDeletedCount();
	}

	@Override
	public int updateIaSort(String id, int sort) {
		Query query = new Query(Criteria.where("_id").is(id));
		
		Update update = new Update();
		update.set("sort", sort);
		
		return (int) mongoTemplate.updateFirst(query, update, COLLECTION_NAME).getModifiedCount();
	}

	@Override
	public int updateIaState(IaDTO dto) {
		// TODO Auto-generated method stub
		Query query = new Query(Criteria.where("_id").is(dto.getId()));
		
		Update update = new Update();
		
		update.set("text", dto.getText());
		update.set("link", dto.getLink());
		update.set("publish_state", dto.getPublish_state());
		update.set("confirm_state", dto.getConfirm_state());
		update.set("update_date", dto.getUpdate_date());
		
		return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}
}
