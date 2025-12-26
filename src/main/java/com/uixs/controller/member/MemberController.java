package com.uixs.controller.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uixs.model.member.dto.MemberDTO;
import com.uixs.service.member.MemberService;


@Controller
@RequestMapping("/member/*")
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	@RequestMapping (value = {"/member/list.do"})
	public String memberListPg(Model model, String page) {
		
		model.addAttribute("page", page);
		
		return "/member/member0100";
	}
	
	@RequestMapping (value = {"/member/insert.do"})
	public String memberInsertPg(Model model, String page) {
		
		model.addAttribute("vmode", "insert");
		model.addAttribute("page", page);
		
		return "/member/member0200";
	}
	
	@RequestMapping (value = {"/member/modify.do"}, method=RequestMethod.POST)
	public String memberModifyPg(Model model, @RequestParam String userid, String page) {
		
		model.addAttribute("vmode", "modify");
		model.addAttribute("userid", userid);
		model.addAttribute("page", page);
		
		return "/member/member0200";
	}
	
	/**
	 * 최초 관리자 정보 디베에 저장
	 * @param userVo
	 */
	@RequestMapping (value = {"/member/admin_insert.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public MemberDTO adminInsertData(MemberDTO user) {
		
		MemberDTO result = new MemberDTO();
		
		try {
			MemberDTO admin = memberService.memberSelectOne("admin");
			
			// 관리자 admin 이 없는경우 admin 유저 추가
			if (admin != null) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				
				String securePwd = encoder.encode(user.getPassword());
				
				user.setPassword(securePwd);
				user.setAuth("admin");
				user.setUsername("관리자");
				
				result = memberService.memberInsert(user);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 사용자 정보 저장
	 * @param user
	 * @return
	 */
	@RequestMapping (value = {"/member/member_insert.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public MemberDTO memberInsertData(MemberDTO user) {
		
		MemberDTO result = new MemberDTO();
		
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			String securePwd = encoder.encode(user.getPassword());
			
			user.setPassword(securePwd);
			
			result = memberService.memberInsert(user);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping (value = {"/member/total_cnt.dat"}, method=RequestMethod.GET)
	@ResponseBody
	public int memberTotalCount() {
		int totCnt = 0;
		try {
			totCnt = memberService.memberTotalCount();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return totCnt;
	}
	
	/**
	 * 멤버 목록 조회
	 * @param start : 페이징 시작 시점 mongodb skip 에 사용
	 * @param limit : 가져올개시물수 10
	 * @return
	 */
	@RequestMapping (value = {"/member/list.dat"}, method=RequestMethod.GET)
	@ResponseBody
	public List<MemberDTO> memberList(Long start, int limit) {
		
		List<MemberDTO> resultList = new ArrayList<MemberDTO>();
		
		try {
			resultList = memberService.memberList(start, limit);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	@RequestMapping (value = {"/member/memberWithPart.dat"}, method=RequestMethod.GET) 
	@ResponseBody
	public List<MemberDTO> memberWithPart(String part) {
		List<MemberDTO> resultList = new ArrayList<MemberDTO>();
		
		try {
			resultList = memberService.memberWithPart(part);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return resultList;
	}
	
	@RequestMapping (value = {"/member/id_check.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public MemberDTO userIdCheck(String userid) {
		
		MemberDTO result = new MemberDTO();
		
		try {
			result = memberService.memberSelectOne(userid);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@RequestMapping (value= {"/member/passwordMatch.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public boolean passwordMatch(@RequestParam String userid, @RequestParam String old_password) {
		
		boolean match = false;
		
		try {
			MemberDTO member = memberService.memberSelectOne(userid);
			
			if (member != null) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				
				match = encoder.matches(old_password, member.getPassword());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return match;
	}
	
	/**
	 * 사용자 정보 수정
	 * @param user
	 * @return
	 */
	@RequestMapping (value = {"/member/member_update.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public int memberUpdateData(MemberDTO user) {
		
		int cnt = 0;
		
		try {
			if (user.getPassword() != null && !user.getPassword().isEmpty()) {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				String securePwd = encoder.encode(user.getPassword());
				
				user.setPassword(securePwd);
			}
			
			cnt = memberService.memberUpdate(user);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return cnt;
	}
	
	/**
	 * 사용자 정보 삭제
	 * @param user
	 * @return
	 */
	@RequestMapping (value = {"/member/member_delete.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public int memberDeleteData(@RequestParam String id) {
		
		int cnt = 0;
		
		try {
			cnt = memberService.memberDelete(id);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return cnt;
	}
	
//	@RequestMapping("login.do")
//	public String login() {
//		return "member/login";
//	}
//	
//	@RequestMapping("join.do")
//	public String join() {
//		return "member/join";
//	}
//	
//
//	
//	@RequestMapping("logout.do")
//	public String logout(HttpSession session) {
//		session.invalidate();
//		
//		return "redirect:/member/login.do";
//	}
}
