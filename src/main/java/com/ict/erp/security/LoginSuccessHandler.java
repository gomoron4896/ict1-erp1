package com.ict.erp.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		System.out.println(getReturnUrl(request, response)); 
		ObjectMapper om = new ObjectMapper();

		Map<String, String> map = new HashMap<String, String>();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName(); // get logged in username
		request.setAttribute("username",currentUserName());
		System.out.println(getReturnUrl(request, response)); 
		map.put("message","로그인 성공");
		map.put("returnUrl",getReturnUrl(request,response));
		// {"success" : true, "returnUrl" : "..."}
		String jsonString = om.writeValueAsString(map);

		OutputStream out = response.getOutputStream();
		out.write(jsonString.getBytes());
		System.out.println(jsonString.getBytes());
	}

	public static String currentUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		System.out.println(user.getUsername());
		return user.getUsername();
	}

	private String getReturnUrl(HttpServletRequest request, HttpServletResponse response) {
		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);
		if (savedRequest == null) {
			return request.getSession().getServletContext().getContextPath();
		}
		System.out.println("로그인 전 요청 URL :" + savedRequest.getRedirectUrl());
		return savedRequest.getRedirectUrl();
	}
}