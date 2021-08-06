package com.wbh.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/index")
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/indexLogged")   
	public String viewIndexLoggedUser() {
		return "indexLogged";
	}
	
	@GetMapping("/admin/")  
	public String viewHomePageAdm() {
		return "admin/index";
	}
	
	@GetMapping("/admin/index")   
	public String viewHome() {
		return "admin/index";
	}
	
	@GetMapping("/login")   
	public String viewLogin() {
		return "login";
	}
	
}
