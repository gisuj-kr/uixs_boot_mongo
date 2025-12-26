package com.uixs.model.member.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.InsertOneResult;
import com.uixs.model.member.dto.MemberDTO;

@Repository
public class MemberDAOImp implements MemberDAO {
	
	//mongodb 에 접속하여 명령어를 실행하는 객체
	@Autowired
	private MongoTemplate mongoTemplate;
	
	String COLLECTION_NAME="member"; //테이블 이름
	
	@Override
	public MemberDTO loginCheck(String userid, String passwd) {
		Query query = new Query(new Criteria("_id").is(userid).and("passwd").is(passwd));
		
		List<MemberDTO> list = mongoTemplate.find(query, MemberDTO.class);
		
		if(list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public MemberDTO memberSelectOne(String userid) {
		new Criteria();
		
		Query query = new Query(Criteria.where("userid").is(userid));
		
		MemberDTO user = mongoTemplate.findOne(query, MemberDTO.class, COLLECTION_NAME);
		
		if(user != null) {
			return user;
		} else {
			return null;
		}
	}
	
	@Override
	public List<MemberDTO> memberList(Long start, int limit) {
		Query query = new Query(Criteria.where("userid").ne("admin"));
		
		query.with(Sort.by(Sort.Direction.DESC, "reg_date")).skip(start).limit(limit); 
		
		List<MemberDTO> list = mongoTemplate.find(query, MemberDTO.class);
		
		return list;
	}
	
	@Override
	public List<MemberDTO> memberWithPart(String part) {
		Query query = new Query(Criteria.where("part").is(part));
		
		List<MemberDTO> list = mongoTemplate.find(query, MemberDTO.class);
		
		return list;
	}
	
	@Override
	public MemberDTO memberInsert(MemberDTO user) {
		MemberDTO result = mongoTemplate.insert(user, COLLECTION_NAME);
		
		return result;
	}
	
	/**
	 * @param user MemberDTO
	 * @param key _id 디비 고유아이디
	 * @throws Exception
	 */
	@Override
	public int memberUpdate(MemberDTO user) {
		Criteria criteria = new Criteria("userid");
		criteria.is(user.getUserid());
		
		Query query = new Query(criteria);
		
		Update update = new Update();
		update.set("username", user.getUsername());
		update.set("team", user.getTeam());
		update.set("part", user.getPart());
		update.set("tel", user.getTel());
		update.set("email", user.getEmail());
		update.set("auth", user.getAuth());
		
		if(user.getPassword() != null && !user.getPassword().isEmpty()) {
			update.set("password", user.getPassword());
		}
		
		return (int) mongoTemplate.updateFirst(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	@Override
	public int memberDelete(String id) {
		System.out.println(id+":: dao 에서 출력");
		Criteria criteria = Criteria.where("_id").is(id);
		
		Query query = new Query(criteria);
		
		return (int) mongoTemplate.remove(query, COLLECTION_NAME).getDeletedCount();
	}
	
	@Override
	public int memberTotalCount() {
		return mongoTemplate.findAll(MemberDTO.class, COLLECTION_NAME).size();
	}
	
	/**
	 * 사용자 전체 수
	 * @return
	 * @throws Exception
	 */
	@Override
	public int selectTotUser() {
		Query query = new Query(Criteria.where("{}"));
		
		return (int) mongoTemplate.count(query, MemberDTO.class, COLLECTION_NAME);
	}
	
	@Override
	public void join(MemberDTO dto) {
		mongoTemplate.insert(dto, COLLECTION_NAME);
	}
}
