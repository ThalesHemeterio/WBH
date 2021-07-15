package com.wbh.site;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("")   // handle HHTP requests
	public String viewHomePage() {
		return "index";
	}
	
	@GetMapping("/search")   // handle HHTP requests
	public String viewSearchPage() {
		return "search";
	}
	
	@GetMapping("/signprofessional")   // handle HHTP requests
	public String viewSignProfessional() {
		return "signprofessional";
	}
	
	@GetMapping("/signclient")   // handle HHTP requests
	public String viewSignClient() {
		return "signclient";
	}
	
	@GetMapping("/index")   // handle HHTP requests
	public String viewIndex() {
		return "index";
	}
}
