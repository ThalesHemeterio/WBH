package com.wbh.professional;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.setting.SettingService;

@Controller
public class ProfessionalController {

	
	
	@GetMapping("/register_professional")
	public String showRegistrationProfessionalForm(Model model) {
		model.addAttribute("pageTitle","Professional Registration");
		model.addAttribute("professional", new Professional());
		
		return "register/register_professional_form";
	}
	
}
