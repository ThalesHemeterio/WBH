package com.wbh.professional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;

@Controller
public class ProfessionalController {

	@Autowired private ProfessionalService service;
	
	
	@GetMapping("/register_professional")
	public String showRegistrationProfessionalForm(Model model) {
		model.addAttribute("pageTitle","Professional Registration");
		model.addAttribute("professional", new Professional());
		
		return "register/register_professional_form";
	}
}
