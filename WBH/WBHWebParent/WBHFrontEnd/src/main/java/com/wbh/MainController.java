package com.wbh;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/index")   // handle HHTP requests
	public String viewHomePage() {
		return "index";
	}

	@GetMapping("/login")   // handle HHTP requests
	public String viewLogin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		return "index";
	}

	@GetMapping("/customers/index")   // handle HHTP requests
	public String viewIndex() {
		return "customers/index";
	}
}
