package com.uixs.controller.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.uixs.model.channel.dto.ChannelDTO;
import com.uixs.service.channel.ChannelService;
import com.uixs.service.work.WorkService;

@Controller
@RequestMapping("/channel/*")
public class ChannelController {
	private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
	
	@Autowired
	ChannelService service;
	
	@Autowired
	WorkService workService;
	
	/**
	 * 채널 목록
	 * @return
	 */
	@RequestMapping (value= {"/channel/list.do"})
	public String chanListView() {
		return "/channel/chan0100";
	}
	
	/**
	 * 채널 등록
	 * @return
	 */
	@RequestMapping (value= {"/channel/insert.do"})
	public String chanInsertView() {
		return "/channel/chan0200";
	}
	
	/**
	 * 채널 수정
	 * @return
	 */
	@RequestMapping (value= {"/channel/update.do"}, method = RequestMethod.POST)
	public String chanUpdateView(@RequestParam("id") String id, Model model) {
		
		System.out.println("id::"+id);
		ChannelDTO dto = service.selectChannelOne(id);
		
		model.addAttribute("channel", dto);
		
		return "/channel/chan0300";
	}
	
	/**
	 * 디비에서 채널 목록 검색
	 * @return
	 */
	@RequestMapping (value= {"/channel/list.dat"}, method = RequestMethod.POST)
	@ResponseBody
	public List<ChannelDTO> channelsData() {
		List<ChannelDTO> returnList = new ArrayList<ChannelDTO>();
		
		try {
			List<ChannelDTO> channels = service.channels();
			
			if (channels.size() > 0) {
				
				for(ChannelDTO channel : channels) {
					int pending_cnt = workService.getWorkCntWithSiteCode(channel.getCode(), "PENDING", "", "");
					int working_cnt = workService.getWorkCntWithSiteCode(channel.getCode(), "WORKING", "", "");
					int complete_cnt = workService.getWorkCntWithSiteCode(channel.getCode(), "COMPLETE", "", "");
					
					channel.setReq_pending_cnt(pending_cnt);
					channel.setReq_working_cnt(working_cnt);
					channel.setReq_complete_cnt(complete_cnt);
					
					returnList.add(channel);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return returnList;
	}

	/**
	 * 채널 등록 process 처리후 완료페이지로 redirect
	 * */
	@RequestMapping (value= {"/channel/insertJson.dat"}, method = RequestMethod.POST) // 테스트
	@ResponseBody
	public void insertData(@RequestBody ChannelDTO dto) { // 테스트
		
		try {
			service.insertChannel(dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 채널 등록 process 처리후 완료페이지로 redirect
	 * */
	@RequestMapping (value= {"/channel/insert.dat"}, method = RequestMethod.POST) // 테스트
	public String insertData(ChannelDTO dto, RedirectAttributes redirect) { // 테스트

		try {
			if (dto.getIa_filepath() == null) {
				dto.setIa_filepath("/static/xlsx/ia_"+dto.getCode()+".xlsx");
			}
			
			service.insertChannel(dto);
			
			redirect.addFlashAttribute("param", dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/channel/insert_complete.do";
	}
	
	/**
	 * 채널 등록 완료 페이지
	 * */
	@RequestMapping (value= {"/channel/insert_complete.do"})
	public String insertEndView(HttpServletRequest request, Model model) {
		
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		
		ChannelDTO param = new ChannelDTO();
		
        if(flashMap!=null) {
            
        	param = (ChannelDTO)flashMap.get("param");
        	
        	model.addAttribute("name", param.getName());
        	model.addAttribute("code", param.getCode());
        	model.addAttribute("device", param.getDevice());
        	model.addAttribute("cuser", param.getCuser());
        	model.addAttribute("doc_base", param.getDoc_base());
        	model.addAttribute("ia_filepath", param.getIa_filepath());
        	
        }
		
		return "/channel/chan0201";
	}
	
	
	/**
	 * 채널 하나 검색
	 * */
	@RequestMapping (value = {"/channel/channel_one.dat"}, method=RequestMethod.POST) 
	@ResponseBody
	public ChannelDTO channelOneData(@RequestBody ChannelDTO dto){
		
		System.out.println("dtocode=="+dto.getCode());
		
		ChannelDTO retDto = service.selectCodeOne(dto.getCode());
		
		if (retDto == null) {
			retDto = new ChannelDTO();
		}
		
		return retDto;
	}
	
	/**
	 * 채널정보 수정 process
	 * */
	@RequestMapping (value = {"/channel/updateJson.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public int updateData(@RequestBody ChannelDTO dto) {
		int cnt = 0;
		
		try {
			cnt = service.updateChannel(dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return cnt;
	}
	/**
	 * 채널정보 수정 process
	 * */
	@RequestMapping (value = {"/channel/update.dat"}, method=RequestMethod.POST)
	public String updateData(ChannelDTO dto, RedirectAttributes redirect) {
		
		try {
			int cnt = service.updateChannel(dto);
			
			System.out.println(cnt);
			
			redirect.addFlashAttribute("param", dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/channel/update_complete.do";
	}
	
	/**
	 * 채널 수정완료
	 * */
	@RequestMapping (value = {"/channel/update_complete.do"})
	public String updateEndView(HttpServletRequest request, Model model) {
		
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		
		ChannelDTO param = new ChannelDTO();
		
        if(flashMap!=null) {
            
        	param =(ChannelDTO)flashMap.get("param");
        	
        	model.addAttribute("name", param.getName());
        	model.addAttribute("code", param.getCode());
        	model.addAttribute("device", param.getDevice());
        	model.addAttribute("cuser", param.getCuser());
        	model.addAttribute("doc_base", param.getDoc_base());
        	model.addAttribute("ia_filepath", param.getIa_filepath());
        }
        
		return "/channel/chan0301";
	}
	
	@RequestMapping (value = {"/channel/delete.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public int deleteChannel(@RequestBody ChannelDTO dto) {
		int delCnt = 0;
		
		try {
			delCnt = service.removeChannel(dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return delCnt;
	}
	
	@RequestMapping (value = {"/channel/working_cnt.dat"}, method=RequestMethod.POST)
	@ResponseBody
	public int workingChannel(@RequestBody ChannelDTO dto) {
		int delCnt = 0;
		
		try {
			delCnt = service.removeChannel(dto);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return delCnt;
	}
}