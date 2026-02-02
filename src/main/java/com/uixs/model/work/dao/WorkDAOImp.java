package com.uixs.model.work.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.mongodb.BasicDBObject;
import com.uixs.controller.work.WorkController;
import com.uixs.model.work.dto.WorkDTO;
import com.uixs.model.work.dto.WorkStateDTO;

@Repository
public class WorkDAOImp implements WorkDAO {
	private static final Logger logger = LoggerFactory.getLogger(WorkController.class);
	//mongodb 에 접속하여 명령어를 실행하는 객체
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value("${mongodb.collection.request-list}")
	String COLLECTION_NAME; //테이블 이름
	
	@Override
	public WorkDTO insert(WorkDTO workDto) throws Exception {
		
		workDto.setRequest_state("PENDING");
		
		WorkDTO result = mongoTemplate.insert(workDto, COLLECTION_NAME);
		
		return result;
	}
	
	/**
	 * 신규 아이디 저장을 위한 아이디 검색
	 * @param request_type
	 * @return
	 */
	@Override
	public String selectRequestId(String request_type) {
		if (mongoTemplate.findAll(WorkDTO.class, COLLECTION_NAME).size() <= 0 ) {
			return null;
		}
		else {
			AggregationOperation project = Aggregation.project()
					.andExpression("substr(request_id, 10, -1)").as("request_id");
			
			AggregationOperation sort = Aggregation.sort(Sort.Direction.DESC, "request_id");
			
			AggregationOperation limit = Aggregation.limit(1);
			
			Aggregation aggregation = Aggregation.newAggregation(project, sort, limit);
			
			AggregationResults<WorkDTO> result = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, WorkDTO.class);
			
			WorkDTO firstResult = result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);
			
			return firstResult.getRequest_id();
		}
	}
	

	/**
	 * 작업요청시 선택한 ia 항목저장
	 * @param requestIaParam
	 * @throws Exception
	 */
	@Override
	public void insertRequestIa(Map<String, Object> requestIaParam) throws Exception {
//		mapper.insertRequestIa(requestIaParam);
	}
	
	/**
	 * 작업요청 내역
	 * @return
	 */
	@Override
	public List<WorkDTO> selectRequestList(WorkDTO work) {
		Criteria criteria = new Criteria();
		criteria.orOperator(
				Criteria.where("request_state").is("PENDING"),
				Criteria.where("request_state").is("CANCEL")
		);
		
		if (work.getSite_code() != null) {
			criteria = new Criteria().andOperator(criteria, Criteria.where("site_code").is(work.getSite_code()));
		}
		
		String searchKey = work.getSearch_key();
		String searchWord = work.getSearch_word();
		
		
		if (searchKey != null && !searchKey.isEmpty() && searchWord != null) {
			if ("ALL".equals(searchKey)) {
				criteria = new Criteria().andOperator(
						criteria,
						new Criteria().orOperator(
								Criteria.where("request_id").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_title").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_content").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("username").regex(".*" + searchWord + ".*", "i")
						)
				);
			} else {
				criteria = new Criteria().andOperator(
						criteria,
						Criteria.where(searchKey).regex(".*" + searchWord + ".*", "i") //포함하는 컬럼
				);
			}
		}
		
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.DESC, "regdate"),
				Aggregation.skip(Long.valueOf(work.getStart())),
				Aggregation.limit(Long.valueOf(work.getLimit()))
		);
		
		AggregationResults<WorkDTO> result = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, WorkDTO.class);
		
		return result.getMappedResults();
	}
	
	/**
	 * 이번달 업무내역 조회 - 다운로드에 사용
	 */
	@Override
	public List<WorkDTO> selectListWhereDate(String startDate, String endDate) {
		
		YearMonth today1 = YearMonth.now();
		LocalDate start1 = today1.atDay(1);
		LocalDate end1 = today1.atEndOfMonth();
		
		if (startDate != null && endDate != null) {
			start1 = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
			end1 = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		}
		
		Criteria criteria = new Criteria();
		criteria.orOperator(
				Criteria.where("part.work_content.part_work_sday").gte(start1).lte(end1),
				new Criteria().andOperator(
					Criteria.where("part.work_content.part_work_sday").lte(end1),
					Criteria.where("part.state").ne("COMPLETE")
				)
		);
		
		criteria = new Criteria().andOperator(
				criteria,
				Criteria.where("request_state").ne("DELETE")
		);
		
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.ASC, "regdate")
		);
		
		AggregationResults<WorkDTO> result = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, WorkDTO.class);
		
		return result.getMappedResults();
	}
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	@Override
	public WorkDTO selectRequestOne(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		
		return mongoTemplate.findOne(query, WorkDTO.class, COLLECTION_NAME);
	}
	
	/**
	 * 작업요청 내역 상세정보
	 * @param request_id
	 * @return
	 */
	@Override
	public WorkDTO selectWorkOne(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		
		return null;
	}
	
	/**
	 * 요청 작업의 수락 혹은 거절 상태 저장
	 * @param workVo
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateRequestWorkState(WorkDTO work) throws Exception {
		
		Criteria criteria = Criteria.where("_id").is(work.getId());
		
		Query query = new Query(criteria);
		Update update = new Update();
		
		if (work.getRequest_state() != null) update.set("request_state", work.getRequest_state());
		
		// 작업중인 파트 업데이트
		if (work.getWorking_part() != null) update.set("working_part", work.getWorking_part());
		
		// 기획 작업 상태 수정
		if (work.getPlan_state() != null) {
			Object obj = work.getPlan_state();
			
			for(Field field : obj.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(obj);
				
				//if (value == null) continue;
				
				update.set("plan_state."+field.getName(), value);
			}
		}
		
		// 퍼블 작업 상태 수정
		if (work.getPublish_state() != null) {
			Object obj2 = work.getPublish_state();
			
			for(Field field : obj2.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(obj2);
				
				//if (value == null) continue;
				
				update.set("publish_state."+field.getName(), value);
			}
		}
		
		// 디자인 작업 상태 수정
		if (work.getDesign_state() != null) {
			Object obj3 = work.getDesign_state();
			
			for(Field field : obj3.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(obj3);
				
				//if (value == null) continue;
				
				update.set("design_state."+field.getName(), value);
			}
		}
		
//		update.set("work_state", work.getWork_state());
		
//	    template.updateFirst(query, update, "컬렉션");
		
		if (work.getResponse_date() != null) {
			update.set("response_date", work.getResponse_date());
		}
		if (work.getComplete_date() != null) {
			update.set("complete_date", work.getComplete_date());
		}
		if (work.getCancel_content() != null) {
			update.set("cancel_content", work.getCancel_content());
		}
		if (work.getRequest_date() != null) {
			update.set("request_date", work.getRequest_date());
		}
		if (work.getRequest_complete_date() != null) {
			update.set("request_complete_date", work.getRequest_complete_date());
		}
		
		if (work.getPart() != null) {
			update.set("part", work.getPart());
		}
		
		return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	
	/**
	 * 요청작업 수정 (요청 작업 전체 수정)
	 */
	@Override
	public int modifyRequest(WorkDTO work) {
		
		Criteria criteria = Criteria.where("_id").is(work.getId());
		
		Query query = new Query(criteria);
		Update update = new Update();
		
		 // 리플렉션으로 null이 아닌 필드만 업데이트
	    Field[] fields = work.getClass().getDeclaredFields();
	    for (Field field : fields) {
	        field.setAccessible(true);
	        try {
	            Object value = field.get(work);
	            if (value != null && !field.getName().equals("id")) {
	                update.set(field.getName(), value);
	            }
	        } catch (IllegalAccessException e) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
	        	logger.error("예상치 못한 접근 오류: {}", field.getName(), e);
	        }
	    }
	    
	    return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	/**
	 * 진행중인 작업 목록 조회
	 * @return
	 */
	@Override
	public List<WorkDTO> selectProcessList(WorkDTO work) {
		Criteria criteria = Criteria.where("request_state").is("WORKING");
		
		if (work.getSite_code() != null) {
			criteria = new Criteria().andOperator(
					criteria, 
					Criteria.where("site_code").is(work.getSite_code())
			);
		}
		
		String searchKey = work.getSearch_key();
		String searchWord = work.getSearch_word();
		
		if (searchKey != null && !searchKey.isEmpty() && searchWord != null) {
			if ("ALL".equals(searchKey)) {
				criteria = new Criteria().andOperator(
						criteria,
						new Criteria().orOperator(
								Criteria.where("request_id").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_title").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_content").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("username").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("part.worker").regex(".*" + searchWord + ".*", "i")
						)
				);
			} else {
				criteria = new Criteria().andOperator(
						criteria,
						Criteria.where(searchKey).regex(".*" + searchWord + ".*", "i")
				);
			}
		}
		
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.DESC, "regdate"),
				Aggregation.skip(Long.valueOf(work.getStart())),
				Aggregation.limit(Long.valueOf(work.getLimit()))
		);
		
		AggregationResults<WorkDTO> agriResult = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, WorkDTO.class);
		
		return agriResult.getMappedResults();
	}
	

	
	/**
	 * 작업진행 항목 저장
	 * @param request_id
	 * @param state
	 * @throws Exception
	 */
	@Override
	public void insertWorkList(String request_id, String state) throws Exception {
		WorkDTO work = new WorkDTO(); 
		
		work.setRequest_id(request_id);
		work.setState(state);
		
//		workDao.insertWorkList(work);
//		
//		return work.getWork_id();
	}


	@Override
	public List<WorkDTO> selectCompleteList(String site_code) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<WorkDTO> selectCompleteList(WorkDTO work) {
		// TODO Auto-generated method stub
		
		Criteria criteria = Criteria.where("request_state").is("COMPLETE");
		
		if (work.getSite_code() != null) {
			criteria = new Criteria().andOperator(
					criteria, 
					Criteria.where("site_code").is(work.getSite_code())
			);
		}
		
		String searchKey = work.getSearch_key();
		String searchWord = work.getSearch_word();
		
		if (searchKey != null && !searchKey.isEmpty() && searchWord != null) {
			if ("ALL".equals(searchKey)) {
				criteria = new Criteria().andOperator(
						criteria,
						new Criteria().orOperator(
								Criteria.where("request_id").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_title").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_content").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("username").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("part.worker").regex(".*" + searchWord + ".*", "i")
						)
				);
			} else {
				criteria = new Criteria().andOperator(
						criteria,
						Criteria.where(searchKey).regex(".*" + searchWord + ".*", "i")
				);
			}
		}
		
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.DESC, "regdate"),
				Aggregation.skip(Long.valueOf(work.getStart())),
				Aggregation.limit(Long.valueOf(work.getLimit()))
		);
		
		AggregationResults<WorkDTO> agriResult = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, WorkDTO.class);
		
		return agriResult.getMappedResults();
	}

	@Override
	public int deleteWorkState(String id) throws Exception {
		Query query = new Query(Criteria.where("_id").is(id));
		Update update = new Update();
		
		update.set("request_state", "DELETE");
		// TODO Auto-generated method stub
		return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	@Override
	public int getWorkCntWithSiteCode(String site_code, String request_state, String searchKey, String searchWord) {
		Criteria criteria = Criteria.where("request_state").is(request_state);
		
		if (site_code != null) {
			criteria = new Criteria().andOperator(
					criteria,
					Criteria.where("site_code").is(site_code)
			);
		}
	
		// 검색어가 있는경우
		if (searchKey != null && !searchKey.isEmpty() && searchWord != null) {
			if ("ALL".equals(searchKey)) {
				criteria = new Criteria().andOperator(
						criteria,
						new Criteria().orOperator(
								Criteria.where("request_id").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_title").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("request_content").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("username").regex(".*" + searchWord + ".*", "i"),
								Criteria.where("part.worker").regex(".*" + searchWord + ".*", "i")
						)
				);
			} else {
				criteria = new Criteria().andOperator(
						criteria,
						Criteria.where(searchKey).regex(".*" + searchWord + ".*", "i")
				);
			}
		}
		
		Query query = new Query(criteria);
		
		return (int) mongoTemplate.count(query, COLLECTION_NAME);
	}
	
	/**
	 * 검수요청 건수 
	 */
	@Override
	public int selectConfirmCount(String site_code) {
		Criteria criteria = new Criteria().orOperator(
				Criteria.where("plan_state.state").is("CONFIRM_REQUEST"),
				Criteria.where("design_state.state").is("CONFIRM_REQUEST"),
				Criteria.where("publish_state.state").is("CONFIRM_REQUEST")
		);
		
		if (site_code != null) {
			criteria = new Criteria().andOperator(
					criteria,
					Criteria.where("site_code").is(site_code)
			);
		}
		
		Query query = new Query(criteria);
		
		return (int) mongoTemplate.count(query, COLLECTION_NAME);
	}

	@Override
	public void insertWorkState(WorkDTO work) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int updateWorkState(WorkDTO work) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public int deleteWorkList(String id) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> selectRequestWorkWithMonth(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int updateData(WorkDTO dto) {
		Criteria criteria = Criteria.where("_id").is(dto.getId());
		
		Query query = new Query(criteria);
		Update update = new Update();
		
		// 제목 수정
		if (dto.getRequest_title() != null && !dto.getRequest_title().isEmpty()) {
			update.set("request_title", dto.getRequest_title());
		}
		
		// 내용 수정
		if (dto.getRequest_content() != null && !dto.getRequest_content().isEmpty()) {
			update.set("request_content", dto.getRequest_content());
		}
		
		// 필요인력(공수) 수정
		if (dto.getNeed_workers() != null) update.set("need_workers", dto.getNeed_workers());
		
		return (int) mongoTemplate.updateMulti(query, update, COLLECTION_NAME).getModifiedCount();
	}
	
	/**
     * 특정 WorkDTO 문서의 part 배열에서 name이 partName 인 요소를 삭제합니다.
     * @param workId 삭제할 문서의 ID
     * @param PartName 삭제할 파트 명
     * @return 수정된 문서의 수
     */
	@Override
    public int removePartByName(String reqId, String partName) { // workId는 WorkDTO의 _id와 동일한 타입이어야 합니다.
        // 쿼리: _id가 workId인 문서를 찾습니다.
        Criteria criteria = Criteria.where("_id").is(reqId);
        Query query = new Query(criteria);

        // 업데이트: part 배열에서 name이 partName 인 요소를 $pull 합니다.
        Update update = new Update().pull("part", new Criteria("name").is(partName));

        // updateMulti를 사용하여 조건에 맞는 모든 문서를 업데이트할 수도 있지만,
        // _id로 찾으면 보통 하나의 문서만 해당됩니다.
        // 하나의 문서만 업데이트하는 것이라면 updateFirst 또는 updateMulti를 사용할 수 있습니다.
        // 여기서는 하나의 문서만 업데이트하는 경우를 가정합니다.
        return (int) mongoTemplate.updateFirst(query, update, COLLECTION_NAME).getModifiedCount();
    }
	
	
	 /**
     * request_state가 "WORKING"이고 part 배열 중 state가 "WORKING"인 데이터 조회
     * @return 조회된 결과 리스트
     */
	/*
    public List<WorkDTO> getWorkingRequestsWithWorkingParts() {
        
        // 1단계: request_state가 "WORKING"인 문서 필터링
        MatchOperation matchRequestState = Aggregation.match(
            Criteria.where("request_state").is("WORKING")
        );
        
        // 2단계: part 배열을 개별 문서로 분리
        UnwindOperation unwindParts = Aggregation.unwind("part");
        
        // 3단계: part.state가 "WORKING"인 것만 필터링
        MatchOperation matchPartState = Aggregation.match(
            Criteria.where("part.state").is("WORKING")
        );
        
        // Aggregation 파이프라인 구성
        Aggregation aggregation = Aggregation.newAggregation(
            matchRequestState,
            unwindParts,
            matchPartState
        );
        
        // 쿼리 실행
        AggregationResults<Map> results = mongoTemplate.aggregate(
            aggregation, "request_list", Map.class
        );
        
        return results.getMappedResults();
    }
    */
    
    /**
     * 대안 방법: 기본 쿼리 사용 (전체 조회 후 필터링)
     * @return 조회된 결과 리스트
     */
    public List<WorkDTO> getWorkingRequestsAlternative() {
        
        Query query = new Query();
        query.addCriteria(Criteria.where("request_state").is("WORKING"));
        
        List<WorkDTO> results = mongoTemplate.find(query, WorkDTO.class, COLLECTION_NAME);
        
        // 결과에서 part 배열 필터링은 JavaScript에서 처리
        return results;
    }
	
}
