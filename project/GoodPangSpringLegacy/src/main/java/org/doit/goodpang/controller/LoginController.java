package org.doit.goodpang.controller;

import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/login")
public class LoginController {

	@GetMapping
	public String login(
	        @RequestParam(
	            value = "redirect",
	            required = false
	        ) String redirect,
	        Authentication authentication,
	        HttpSession session) {

	    if (authentication != null
	            && authentication.isAuthenticated()
	            && !(authentication
	                    instanceof AnonymousAuthenticationToken)) {

	        return "redirect:/";
	    }
	    if (redirect != null
	            && !redirect.isBlank()
	            && redirect.startsWith("/")) {

	        session.setAttribute(
	                "redirectAfterLogin",
	                redirect
	        );
	    }

	    return "login";
	}
}