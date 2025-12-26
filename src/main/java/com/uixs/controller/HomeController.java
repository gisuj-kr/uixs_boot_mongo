package com.uixs.controller;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping("/")
	public String home(Locale locale) {
		
		return "/home";
	}
	
	@RequestMapping("/guide/guide.do")
	public String guide(Locale locale) {
		
		return "/guide/guide";
	}
	
	@GetMapping(value = {"/", "/{path:^(?!static|api).*$}", "/{path:^(?!static|api).*$}/{subpath:[^\\.]*}"})
    public String forwardToIndex() {
        return "forward:/static/index.html";
    }
	
//	@RequestMapping("/login")
//	public String login(Locale locale) {
//		
//		return "/member/logi0100";
//	}
}
