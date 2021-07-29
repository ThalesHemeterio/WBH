package com.wbh.professional;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.wbh.Utility;
import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.setting.EmailSettingBag;
import com.wbh.setting.SettingService;

@Controller
public class ProfessionalController {

	@Autowired private ProfessionalService service;
	@Autowired private SettingService settingService;
	
	
	@GetMapping("/register/register_professional")
	public String showRegistrationProfessionalForm(Model model) {
		model.addAttribute("pageTitle","Professional Registration");
		model.addAttribute("professional", new Professional());
		
		return "register/register_professional_form";
	}
	
	@PostMapping("/create_professional")
	public String creatProfessional(Professional professional, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		service.registerProfessional(professional);
		sendVerificationEmail(request,professional);
		
		model.addAttribute("pageTitle","Registration Succeeded");
		
		return "/register/register_success";
	}
	
	private void sendVerificationEmail(HttpServletRequest request, Professional professional) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = professional.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(),emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", professional.getFullName());
		
		String verifyURL = Utility.getSiteURL(request)+ "/verify_p?code=" +professional.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content,true);
		
		mailSender.send(message);
		
		System.out.println("to address:"+ toAddress);
		System.out.println("Verify URL:"+ verifyURL);
		
	}
	
	@GetMapping("/verify_p")
	public String verifyAccount(@Param("code") String code, Model model) {
		boolean verified = service.verify(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
}
