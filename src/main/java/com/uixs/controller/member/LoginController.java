package com.uixs.controller.member;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.uixs.service.member.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uixs.model.member.dto.MemberDTO;

@Controller
@RequestMapping("/member/*")
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	MemberService memberService;

	
	@RequestMapping(value = {"/member/login.do"})
	public String loginView(
			HttpServletRequest request,
			HttpServletResponse response,
			@CookieValue(value="loginId", required = false) Cookie loginIdCookie) {
		
		logout(request, response, loginIdCookie);
		//logger.info("userdir:" + System.getProperty("user.dir"));
		MemberDTO admin = memberService.memberSelectOne("admin");
		
		// 관리자 admin 이 없는경우 admin 생성화면으로 이동
		if (admin == null) {
			return "/member/set_admin";
		} else {
			return "/member/logi0100";
		}
	}
	
	/**
	 * 유저 로그인
	 */
	@RequestMapping(value = {"/member/login.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> loginData(
			MemberDTO param, 
			HttpServletRequest request,
			HttpServletResponse response) {
		
		//HttpServletRequest request =((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);
		
		Map<String, Object> loginRs = new HashMap<String, Object>();
		
		try {
			MemberDTO loginUser = memberService.memberSelectOne(param.getUserid());
			boolean passwordMatch = false;
			
			if (loginUser == null) {
				loginRs.put("LOGIN", "FAIL");
				loginRs.put("FAIL_TYPE", "ID");
			}
			else {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				passwordMatch = encoder.matches(param.getPassword(), loginUser.getPassword());
				
				if (!passwordMatch) {
					loginRs.put("LOGIN", "FAIL");
					loginRs.put("FAIL_TYPE", "PASSWORD");
				}
				else {
					setLoginSession(session, loginUser);
					
					/*
					 * session.setMaxInactiveInterval(1800); // 365 * 24 * 60 * 60
					 * session.setAttribute("userid", loginUser.getUserid());
					 * session.setAttribute("username", loginUser.getUsername());
					 * session.setAttribute("team", loginUser.getTeam());
					 * session.setAttribute("part", loginUser.getPart());
					 * session.setAttribute("auth", loginUser.getAuth());
					 */
					
					Cookie loginCookie = new Cookie("loginId", loginUser.getUserid());
					loginCookie.setMaxAge(-1);
					response.addCookie(loginCookie);
					
					loginRs.put("LOGIN", "SUCCESS");
					loginRs.put("FAIL_TYPE", "NONE");
					
					//logger.info("loginid::"+ ((UserVO) session.getAttribute("LOGIN_INFO")).getUserid());
				}
			}
			
		}
		catch(Exception e) {
			loginRs.put("LOGIN", "FAIL");
			loginRs.put("FAIL_TYPE", "SERVER");
			
			e.printStackTrace();
		}
		
		return loginRs;
	}
	
	
	public void setLoginSession(HttpSession session, MemberDTO loginUser) {
		logger.info("Member DTO {}", loginUser);
		
		session.setMaxInactiveInterval(1800); //	365 * 24 * 60 * 60
		session.setAttribute("loginSession", loginUser);
		
		/*
		Map<String, String> loginInfoMap = new HashMap<String, String>();
		
		loginInfoMap.put("userid", loginUser.getUserid());
		loginInfoMap.put("username", loginUser.getUsername());
		loginInfoMap.put("team", loginUser.getTeam());
		loginInfoMap.put("part", loginUser.getPart());
		loginInfoMap.put("auth", loginUser.getAuth());
		
		String json = null;
		 try {
			json = new ObjectMapper().writeValueAsString(loginInfoMap);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		session.setAttribute("loginSession", json);
		*/
		
		logger.info("session info: {}", session.getAttribute("loginSession"));
		
	}
	
	public MemberDTO getLoginInfo(HttpSession session) {
		
		MemberDTO user = new MemberDTO();
//		ObjectMapper dtoMapper = new ObjectMapper();
		
		try {
			user = (MemberDTO) session.getAttribute("loginSession");//dtoMapper.readValue((String) session.getAttribute("loginSession"), MemberDTO.class);
			
			return user;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		}
		
//		user.setUserid((String) loginSession.get("userid"));
//		user.setUsername((String) loginSession.get("username"));
//		user.setTeam((String) loginSession.get("team"));
//		user.setPart((String) loginSession.get("part"));
//		user.setAuth((String) loginSession.get("auth"));
		
		
	}
	
	
	/**
	 * userid, 
	 * username, 
	 * team, 팀구분
	 * part,업무구분 PLAN, DESIGN, PUBLISH
	 * auth 권한 WORKER, MANAGER
	 * */
	@RequestMapping(value = {"/member/login_info.dat"}, method=RequestMethod.GET)
	@ResponseBody
	public MemberDTO loginInfoData(
			HttpServletRequest request, 
			@CookieValue(value="loginId", required = false) Cookie loginIdCookie) {
		
		HttpSession session = request.getSession(true);
		
		MemberDTO user = new MemberDTO();
		
		if (session.getAttribute("loginSession") != null && session.getId() != null) {
			
			user = getLoginInfo(session);
		}
		else {
			if (loginIdCookie != null) {
				try {
					MemberDTO loginUser = memberService.memberSelectOne(loginIdCookie.getValue());
					if (loginUser != null) {
						setLoginSession(session, loginUser);
						
						user = getLoginInfo(session);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		user.setPassword(null);
		
		return user;
	}
	
	/*
	 * 로그아웃
	 * */
	@RequestMapping(value = {"/member/logout.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public void logout(
			HttpServletRequest request,
			HttpServletResponse response,
			@CookieValue(value="loginId", required = false) Cookie loginIdCookie) {
		
		HttpSession session = request.getSession(true);
		
		try {
			// 세션 삭제
			if (session.getAttribute("loginSession") != null) {
				session.invalidate();
				System.out.println("logouted");
			}
			
			// 쿠키 삭제
			if (loginIdCookie != null) {
				loginIdCookie.setValue(null);
				loginIdCookie.setMaxAge(0);
				response.addCookie(loginIdCookie);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 권한 확인
	 * @param id
	 * @param session
	 * @return
	 */
	@GetMapping("/checkPermission")
    public ResponseEntity<String> checkUserPermission(@RequestParam("id") String id, HttpSession session) {
        try {
//        	ObjectMapper dtoMapper = new ObjectMapper();
        	
            MemberDTO loginUser = (MemberDTO) session.getAttribute("loginSession");//dtoMapper.readValue((String) session.getAttribute("loginSession"), MemberDTO.class);
            
            if (loginUser == null || loginUser.getUserid() == null) {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }

            if (loginUser.getUserid().equals(id) || loginUser.getUserid().equals("admin")) {
                return new ResponseEntity<>("Authorized", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
            }

        } catch (Exception e) {
        	e.printStackTrace();
            logger.error("Permission check failed: {}", e.getMessage());
            return new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * 관리자 확인
	 * @param session
	 * @return
	 */
	@GetMapping("/checkAuth")
	public ResponseEntity<String> checkAuth(HttpSession session) {
		try {
			MemberDTO loginUser = (MemberDTO) session.getAttribute("loginSession");//dtoMapper.readValue((String) session.getAttribute("loginSession"), MemberDTO.class);
			
			if (loginUser == null || loginUser.getUserid() == null) {
				return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
			}
			
			if (loginUser.getUserid().equals("admin") || loginUser.getAuth().equals("ADMIN")) {
				return new ResponseEntity<>("Authorized", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Permission check failed: {}", e.getMessage());
			return new ResponseEntity<>("Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
