package com.wbh.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/index")   // handle HHTP requests
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/indexLogged")   // handle HHTP requests
	public String viewIndexLoggedUser() {
		return "indexLogged";
	}
	
	@GetMapping("/admin/")   // handle HHTP requests
	public String viewHomePageAdm() {
		return "admin/index";
	}
	
	@GetMapping("/admin/index")   // handle HHTP requests
	public String viewHome() {
		return "admin/index";
	}
	
	@GetMapping("/login")   // handle HHTP requests
	public String viewLogin() {
		return "login";
	}
	
}
