package com.uixs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SampleController {
	@RequestMapping("/smaple")
	public String sample() {
		return "sample";
	}
}
