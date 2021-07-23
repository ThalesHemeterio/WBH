package com.wbh;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("")   // handle HHTP requests
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/login")   // handle HHTP requests
	public String viewLogin() {
		return "login";
	}
	
	@GetMapping("/index")   // handle HHTP requests
	public String viewIndex() {
		return "index";
	}
}
