package com.uixs.service.member;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.uixs.model.member.dto.MemberDTO;

public interface MemberService {
	MemberDTO loginCheck(String userid, String passwd);
	
	MemberDTO memberSelectOne(String userid);
	
	int memberTotalCount();
	
	void join(MemberDTO dto);

	MemberDTO memberInsert(MemberDTO user);
	
	public List<MemberDTO> memberList(Long start, int limit);
	
	public List<MemberDTO> memberWithPart(String part);
	
	int memberUpdate(MemberDTO user);
	
	int memberDelete(String id);
}
