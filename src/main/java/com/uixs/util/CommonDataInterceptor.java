package com.uixs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class CommonDataInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 컨트롤러 실행 전에 수행할 작업
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        // 컨트롤러 실행 후 화면에 모델을 전달하기 전에 수행할 작업
        if (modelAndView != null) {
//        	ModelMap model = modelAndView.getModelMap();
//        	
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    		Date date = new Date();
    		
//    		model.addAttribute("datetime2", sdf.format(date));
        	
            modelAndView.addObject("datetime", sdf.format(date));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        // 뷰 렌더링까지 완료된 후에 수행할 작업
    }
}
