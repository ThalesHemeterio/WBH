package com.wbh.professional;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.wbh.Utility;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;
import com.wbh.session.SessionService;
import com.wbh.setting.EmailSettingBag;
import com.wbh.setting.SettingService;

@Controller
public class ProfessionalController {

	
	@Autowired SessionService sessionService;
	@Autowired private ProfessionalService service;
	@Autowired private SettingService settingService;
	
	@GetMapping("/professionals/index")
	public String showIndexforProfessional() {
		return "professionals/index";
	}
	
	@GetMapping("/register/register_professional")
	public String showRegistrationProfessionalForm(Model model) {
		model.addAttribute("pageTitle","Professional Registration");
		model.addAttribute("professional", new User());
		
		return "register/register_professional_form";
	}
	
	@PostMapping("/create_professional")
	public String creatProfessional(User professional, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		service.registerProfessional(professional);
		sendVerificationEmail(request,professional);
		
		model.addAttribute("pageTitle","Registration Succeeded");
		
		return "/register/register_success";
	}
	
	private void sendVerificationEmail(HttpServletRequest request, User professional) throws UnsupportedEncodingException, MessagingException {
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
	
	@GetMapping("/professionals/search")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/search/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = sessionService.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listSessions = page.getContent();
		
		long startCount =(pageNum-1)*SessionService.USERS_PER_PAGE+1;
		long endCount = startCount+SessionService.USERS_PER_PAGE-1;
		if(endCount >page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		//passing the attributes to the view
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("listSessions",listSessions);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "professionals/search";
	}
}
