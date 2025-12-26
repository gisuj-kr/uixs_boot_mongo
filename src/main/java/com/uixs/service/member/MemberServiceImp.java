package com.uixs.service.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uixs.model.member.dao.MemberDAO;
import com.uixs.model.member.dto.MemberDTO;

@Service
public class MemberServiceImp implements MemberService {
	@Autowired
	private MemberDAO memberDao;
	
	@Override
	public MemberDTO loginCheck(String userid, String passwd) {
		return memberDao.loginCheck(userid, passwd);
	}
	
	@Override
	public MemberDTO memberSelectOne(String userid) {
		return memberDao.memberSelectOne(userid);
	}
	
	@Override
	public MemberDTO memberInsert(MemberDTO user) {
		return memberDao.memberInsert(user);
	}
	
	@Override
	public void join(MemberDTO dto) {
		memberDao.join(dto);
	}
	
	@Override
	public int memberTotalCount() {
		return memberDao.memberTotalCount();
	}
	
	@Override
	public List<MemberDTO> memberList(Long start, int limit) {
		return memberDao.memberList(start, limit);
	}
	
	@Override
	public List<MemberDTO> memberWithPart(String part) {
		return memberDao.memberWithPart(part);
	}	

	@Override
	public int memberUpdate(MemberDTO user) {
		// TODO Auto-generated method stub
		return memberDao.memberUpdate(user);
	}

	@Override
	public int memberDelete(String id) {
		// TODO Auto-generated method stub
		return memberDao.memberDelete(id);
	}
}
