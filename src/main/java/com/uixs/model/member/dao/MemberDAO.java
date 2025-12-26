package com.uixs.model.member.dao;

import java.util.List;
import java.util.Map;

import com.uixs.model.member.dto.MemberDTO;

public interface MemberDAO {
	MemberDTO loginCheck(String userid, String passwd);
	
	MemberDTO memberSelectOne(String userid);
	
	List<MemberDTO> memberList(Long start, int limit);
	
	MemberDTO memberInsert(MemberDTO user);
	
	List<MemberDTO> memberWithPart(String part);
	
	int memberUpdate(MemberDTO user);
	
	int memberDelete(String id);
	
	int memberTotalCount();
	/**
	 * 사용자 전체 수
	 * @return
	 * @throws Exception
	 */
	int selectTotUser();
	
	void join(MemberDTO dto);

}
