package com.uixs.model.work.dao;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uixs.model.work.dto.WorkDTO;
import com.uixs.model.work.dto.WorkDTO.PartInfo;
import com.uixs.model.work.dto.WorkDTO.WorkContent;
import com.uixs.model.work.dto.WorkStateDTO;

@Repository("workDao")
public class WorkDAOImp implements WorkDAO {
	private static final Logger logger = LoggerFactory.getLogger(WorkDAOImp.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private ObjectMapper objectMapper;

	public WorkDAOImp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 숫자 배열이 아닌 ISO-8601 문자열로 저장하도록 설정
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private WorkDTO parseJson(String json) {
        try {
            return objectMapper.readValue(json, WorkDTO.class);
        } catch (Exception e) {
            logger.error("Failed to parse JSON: {}", json);
            logger.error("Error details: ", e);
            return null;
        }
    }

    private String toJson(WorkDTO dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            logger.error("Failed to write WorkDTO to JSON", e);
            return "{}";
        }
    }

	@Override
	public WorkDTO insert(WorkDTO workDto) throws Exception {
		workDto.setRequest_state("PENDING");
        if (workDto.getId() == null || workDto.getId().isEmpty()) {
            workDto.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        // 등록일 누락 방어 로직 추가
        if (workDto.getRegdate() == null) {
            workDto.setRegdate(LocalDateTime.now());
        }
        jdbcTemplate.update("INSERT INTO request_list (id, doc) VALUES (?, ?)", workDto.getId(), toJson(workDto));
        return workDto;
	}
	
	@Override
	public String selectRequestId(String request_type) {
        List<String> jsons = jdbcTemplate.queryForList(
            "SELECT json_extract(doc, '$.request_id') FROM request_list ORDER BY json_extract(doc, '$.request_id') DESC LIMIT 1", String.class);
        if (jsons.isEmpty() || jsons.get(0) == null) return null;
        return jsons.get(0);
	}
	
	@Override
	public void insertRequestIa(Map<String, Object> requestIaParam) throws Exception {
	}
	
	@Override
	public List<WorkDTO> selectRequestList(WorkDTO work) {
        StringBuilder sql = new StringBuilder("SELECT doc FROM request_list WHERE json_extract(doc, '$.request_state') IN ('PENDING', 'CANCEL')");
        List<Object> params = new ArrayList<>();
        
        if (work.getSite_code() != null && !work.getSite_code().isEmpty()) {
            sql.append(" AND json_extract(doc, '$.site_code') = ?");
            params.add(work.getSite_code());
        }
        buildSearchQuery(work, sql, params);
        
        sql.append(" ORDER BY json_extract(doc, '$.regdate') DESC, json_extract(doc, '$.request_id') DESC");
        appendPagination(work, sql, params);
        
        return executeQuery(sql.toString(), params);
	}

    private void buildSearchQuery(WorkDTO work, StringBuilder sql, List<Object> params) {
		String searchKey = work.getSearch_key();
		String searchWord = work.getSearch_word();
		if (searchKey != null && !searchKey.isEmpty() && searchWord != null && !searchWord.isEmpty()) {
			if ("ALL".equals(searchKey)) {
                sql.append(" AND doc LIKE ?");
                params.add("%" + searchWord + "%");
			} else {
                sql.append(" AND json_extract(doc, '$.")
                   .append(searchKey)
                   .append("') LIKE ?");
                params.add("%" + searchWord + "%");
			}
		}
    }

    private void appendPagination(WorkDTO work, StringBuilder sql, List<Object> params) {
        if (work.getLimit() != null && !work.getLimit().isEmpty() && 
            work.getStart() != null && !work.getStart().isEmpty()) {
            try {
                sql.append(" LIMIT ? OFFSET ?");
                params.add(Long.parseLong(work.getLimit()));
                params.add(Long.parseLong(work.getStart()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid pagination parameters: start={}, limit={}", work.getStart(), work.getLimit());
            }
        }
    }

    private List<WorkDTO> executeQuery(String sql, List<Object> params) {
        List<String> jsons = jdbcTemplate.queryForList(sql, String.class, params.toArray());
        List<WorkDTO> list = new ArrayList<>();
        for (String json : jsons) {
            WorkDTO dto = parseJson(json);
            if (dto != null) list.add(dto);
        }
        return list;
    }

	@Override
	public List<WorkDTO> selectListWhereDate(String startDate, String endDate) {
        String sql = "SELECT doc FROM request_list WHERE json_extract(doc, '$.request_state') != 'DELETE' " +
                     "ORDER BY json_extract(doc, '$.site_name') ASC, json_extract(doc, '$.regdate') DESC";
        List<WorkDTO> all = executeQuery(sql, new ArrayList<>());
        
		YearMonth today1 = YearMonth.now();
		LocalDate start1 = today1.atDay(1);
		LocalDate end1 = today1.atEndOfMonth();
		
		if (startDate != null && endDate != null) {
			start1 = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
			end1 = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		}

        final LocalDate fStart = start1;
        final LocalDate fEnd = end1;
        
        // 필터링된 목록 반환
        return all.stream().filter(dto -> {
            if (dto.getPart() == null) return false;
            
            for (PartInfo p : dto.getPart()) {
                if (p.getWork_content() == null || p.getWork_content().isEmpty()) continue;
                
                // [최종 A안 보완] 해당 월에 실제 작업 날짜(sday, eday)가 하나라도 포함된 경우만 추출
                for (WorkContent wc : p.getWork_content()) {
                    LocalDate sday = wc.getPart_work_sday();
                    LocalDate eday = wc.getPart_work_eday();
                    
                    // 착수일 또는 종료일 중 하나가 해당 월 범위 내에 있는 경우 포함
                    boolean sdayIn = (sday != null) && !sday.isBefore(fStart) && !sday.isAfter(fEnd);
                    boolean edayIn = (eday != null) && !eday.isBefore(fStart) && !eday.isAfter(fEnd);
                    
                    if (sdayIn || edayIn) return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
	}
	
	@Override
	public WorkDTO selectRequestOne(String id) {
        List<String> jsons = jdbcTemplate.queryForList("SELECT doc FROM request_list WHERE id = ?", String.class, id);
        return jsons.isEmpty() ? null : parseJson(jsons.get(0));
	}
	
	@Override
	public WorkDTO selectWorkOne(String id) {
		return selectRequestOne(id);
	}
	
	@Override
	public int updateRequestWorkState(WorkDTO work) throws Exception {
        WorkDTO existing = selectRequestOne(work.getId());
        if (existing == null) return 0;

        // 수정 전 최초 등록일 보존
        LocalDateTime originalRegdate = existing.getRegdate();
		
		if (work.getRequest_state() != null) existing.setRequest_state(work.getRequest_state());
		if (work.getWorking_part() != null) existing.setWorking_part(work.getWorking_part());
		
		if (work.getPlan_state() != null) {
            if (existing.getPlan_state() == null) existing.setPlan_state(new WorkStateDTO());
            copyNonNullFields(work.getPlan_state(), existing.getPlan_state());
		}
		if (work.getPublish_state() != null) {
            if (existing.getPublish_state() == null) existing.setPublish_state(new WorkStateDTO());
            copyNonNullFields(work.getPublish_state(), existing.getPublish_state());
		}
		if (work.getDesign_state() != null) {
            if (existing.getDesign_state() == null) existing.setDesign_state(new WorkStateDTO());
            copyNonNullFields(work.getDesign_state(), existing.getDesign_state());
		}
		
		if (work.getResponse_date() != null) existing.setResponse_date(work.getResponse_date());
		if (work.getComplete_date() != null) existing.setComplete_date(work.getComplete_date());
		if (work.getCancel_content() != null) existing.setCancel_content(work.getCancel_content());
		if (work.getRequest_date() != null) existing.setRequest_date(work.getRequest_date());
		if (work.getRequest_complete_date() != null) existing.setRequest_complete_date(work.getRequest_complete_date());
		if (work.getPart() != null) existing.setPart(work.getPart());

        // 최초 등록일 복원 (절대 변경 불가)
        if (originalRegdate != null) {
            existing.setRegdate(originalRegdate);
        }
		
        return jdbcTemplate.update("UPDATE request_list SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
    private void copyNonNullFields(Object source, Object target) {
	    Field[] fields = source.getClass().getDeclaredFields();
	    for (Field field : fields) {
	        field.setAccessible(true);
	        try {
	            Object value = field.get(source);
	            if (value != null) {
	                field.set(target, value);
	            }
	        } catch (IllegalAccessException e) {
	        	logger.error("Failed to copy field: {}", field.getName(), e);
	        }
	    }
    }

	@Override
	public int modifyRequest(WorkDTO work) {
        WorkDTO existing = selectRequestOne(work.getId());
        if (existing == null) return 0;

        // [핵심] 수정 전 DB에 저장된 최초 등록일을 미리 보존합니다.
        // copyNonNullFields가 프론트에서 넘어온 값으로 regdate를 덮어쓸 수 있으므로,
        // 복사 완료 후 원래 등록일로 반드시 복원합니다.
        LocalDateTime originalRegdate = existing.getRegdate();

        copyNonNullFields(work, existing);

        // 최초 등록일은 항상 원본 데이터로 복원 (수정이 있어도 절대 변경 불가)
        if (originalRegdate != null) {
            existing.setRegdate(originalRegdate);
        } else {
            // DB에 기존 등록일이 없는 경우에만 현재 시간 설정 (방어 로직)
            existing.setRegdate(LocalDateTime.now());
        }
	    
        return jdbcTemplate.update("UPDATE request_list SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
	@Override
	public List<WorkDTO> selectProcessList(WorkDTO work) {
        StringBuilder sql = new StringBuilder("SELECT doc FROM request_list WHERE json_extract(doc, '$.request_state') = 'WORKING'");
        List<Object> params = new ArrayList<>();
        
        if (work.getSite_code() != null && !work.getSite_code().isEmpty()) {
            sql.append(" AND json_extract(doc, '$.site_code') = ?");
            params.add(work.getSite_code());
        }
        buildSearchQuery(work, sql, params);
        
        sql.append(" ORDER BY json_extract(doc, '$.regdate') DESC, json_extract(doc, '$.request_id') DESC");
        appendPagination(work, sql, params);
        
        return executeQuery(sql.toString(), params);
	}
	
	@Override
	public void insertWorkList(String request_id, String state) throws Exception {
	}

	@Override
	public List<WorkDTO> selectCompleteList(String site_code) {
        WorkDTO filter = new WorkDTO();
        filter.setSite_code(site_code);
		return selectCompleteList(filter);
	}

	@Override
	public List<WorkDTO> selectCompleteList(WorkDTO work) {
        StringBuilder sql = new StringBuilder("SELECT doc FROM request_list WHERE json_extract(doc, '$.request_state') = 'COMPLETE'");
        List<Object> params = new ArrayList<>();
        
        if (work.getSite_code() != null && !work.getSite_code().isEmpty()) {
            sql.append(" AND json_extract(doc, '$.site_code') = ?");
            params.add(work.getSite_code());
        }
        buildSearchQuery(work, sql, params);
        
        sql.append(" ORDER BY json_extract(doc, '$.regdate') DESC, json_extract(doc, '$.request_id') DESC");
        appendPagination(work, sql, params);
        
        return executeQuery(sql.toString(), params);
	}

	@Override
	public int deleteWorkState(String id) throws Exception {
        WorkDTO existing = selectRequestOne(id);
        if (existing == null) return 0;
        existing.setRequest_state("DELETE");
        return jdbcTemplate.update("UPDATE request_list SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
	@Override
	public int getWorkCntWithSiteCode(String site_code, String request_state, String searchKey, String searchWord) {
        StringBuilder sql = new StringBuilder("SELECT count(*) FROM request_list WHERE json_extract(doc, '$.request_state') = ?");
        List<Object> params = new ArrayList<>();
        params.add(request_state);
        
        if (site_code != null && !site_code.isEmpty()) {
            sql.append(" AND json_extract(doc, '$.site_code') = ?");
            params.add(site_code);
        }
        
        WorkDTO dummy = new WorkDTO();
        dummy.setSearch_key(searchKey);
        dummy.setSearch_word(searchWord);
        buildSearchQuery(dummy, sql, params);
        
        Integer cnt = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return cnt != null ? cnt : 0;
	}
	
	@Override
	public int selectConfirmCount(String site_code) {
        StringBuilder sql = new StringBuilder(
            "SELECT count(*) FROM request_list WHERE (" +
            "json_extract(doc, '$.plan_state.state') = 'CONFIRM_REQUEST' OR " +
            "json_extract(doc, '$.design_state.state') = 'CONFIRM_REQUEST' OR " +
            "json_extract(doc, '$.publish_state.state') = 'CONFIRM_REQUEST')"
        );
        List<Object> params = new ArrayList<>();
        
		if (site_code != null && !site_code.isEmpty()) {
            sql.append(" AND json_extract(doc, '$.site_code') = ?");
            params.add(site_code);
		}
        
        Integer cnt = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return cnt != null ? cnt : 0;
	}

	@Override
	public void insertWorkState(WorkDTO work) throws Exception {
	}

	@Override
	public int updateWorkState(WorkDTO work) throws Exception {
		return 0;
	}

	@Override
	public int deleteWorkList(String id) throws Exception {
		return 0;
	}

	@Override
	public List<Map<String, Object>> selectRequestWorkWithMonth(Map<String, Object> paramMap) {
		// 파라미터에서 year, month 추출 (없으면 이번 달 기본값 사용)
		YearMonth targetMonth;
		try {
			String year  = paramMap.get("year")  != null ? paramMap.get("year").toString()  : null;
			String month = paramMap.get("month") != null ? paramMap.get("month").toString() : null;
			if (year != null && month != null) {
				targetMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
			} else {
				targetMonth = YearMonth.now();
			}
		} catch (Exception e) {
			targetMonth = YearMonth.now();
		}

		// 해당 월의 시작일 ~ 종료일 범위 계산
		String startDate = targetMonth.atDay(1).format(DateTimeFormatter.ISO_DATE);
		String endDate   = targetMonth.atEndOfMonth().format(DateTimeFormatter.ISO_DATE);

		// 완료요청일(request_complete_date)이 해당 월에 포함된 요청 목록 조회
		String sql = "SELECT doc FROM request_list "
				+ "WHERE json_extract(doc, '$.request_state') != 'DELETE' "
				+ "AND json_extract(doc, '$.request_complete_date') >= ? "
				+ "AND json_extract(doc, '$.request_complete_date') <= ?";

		List<String> jsons = jdbcTemplate.queryForList(sql, String.class, startDate, endDate);
		List<Map<String, Object>> result = new ArrayList<>();

		// 캘린더 UI에서 사용할 형태로 변환하여 반환
		for (String json : jsons) {
			WorkDTO dto = parseJson(json);
			if (dto == null) continue;

			Map<String, Object> item = new java.util.HashMap<>();
			item.put("id",                     dto.getId());
			item.put("request_id",             dto.getRequest_id());
			item.put("request_title",          dto.getRequest_title());
			item.put("request_state",          dto.getRequest_state());
			item.put("site_code",              dto.getSite_code());
			item.put("site_name",              dto.getSite_name());
			item.put("request_complete_date",  dto.getRequest_complete_date());  // 완료 요청일
			item.put("request_date",           dto.getRequest_date());           // 업무 요청일
			result.add(item);
		}

		return result;
	}
	
	@Override
	public int updateData(WorkDTO dto) {
        WorkDTO existing = selectRequestOne(dto.getId());
        if (existing == null) return 0;

        // 수정 전 최초 등록일 보존
        LocalDateTime originalRegdate = existing.getRegdate();

		// 제목 수정
		if (dto.getRequest_title() != null && !dto.getRequest_title().isEmpty()) existing.setRequest_title(dto.getRequest_title());
		// 내용 수정
		if (dto.getRequest_content() != null && !dto.getRequest_content().isEmpty()) existing.setRequest_content(dto.getRequest_content());
		// 필요인력(공수) 수정
		if (dto.getNeed_workers() != null) existing.setNeed_workers(dto.getNeed_workers());

        // 최초 등록일 복원 (절대 변경 불가)
        if (originalRegdate != null) {
            existing.setRegdate(originalRegdate);
        }

        return jdbcTemplate.update("UPDATE request_list SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
	}
	
	@Override
    public int removePartByName(String reqId, String partName) {
        WorkDTO existing = selectRequestOne(reqId);
        if (existing == null || existing.getPart() == null) return 0;
        
        boolean removed = existing.getPart().removeIf(p -> partName.equals(p.getName()));
        if (!removed) return 0;
        
        return jdbcTemplate.update("UPDATE request_list SET doc = ? WHERE id = ?", toJson(existing), existing.getId());
    }
	
    public List<WorkDTO> getWorkingRequestsAlternative() {
        return executeQuery("SELECT doc FROM request_list WHERE json_extract(doc, '$.request_state') = 'WORKING'", new ArrayList<>());
    }
}
