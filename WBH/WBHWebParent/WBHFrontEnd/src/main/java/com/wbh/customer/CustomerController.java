package com.wbh.customer;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.Utility;
import com.wbh.common.entity.Review;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;
import com.wbh.professional.ProfessionalService;
//import com.wbh.review.ReviewService;
import com.wbh.session.SessionService;
import com.wbh.setting.EmailSettingBag;
import com.wbh.setting.SettingService;

@Controller
public class CustomerController {
	
	//@Autowired ReviewService service;
	@Autowired SessionService sessionService;
	@Autowired private ProfessionalService professionalService;
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	@GetMapping("register/register_customer")
	public String showRegistrationCustomerForm(Model model) {
		model.addAttribute("pageTitle","Client Registration");
		model.addAttribute("customer", new User());
		
		return "register/register_customer_form";
	}
	
	@GetMapping("/customers/index")
	public String showIndexforCustomer() {
		return "customers/index";
	}
	
	@PostMapping("/create_customer")
	public String creatCustomer(User customer, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		sendVerificationEmail(request,customer);
		
		model.addAttribute("pageTitle","Registration Succeeded");
		
		return "/register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, User customer) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(),emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", customer.getFullName());
		
		String verifyURL = Utility.getSiteURL(request)+ "/verify?code=" +customer.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content,true);
		
		mailSender.send(message);
		
		System.out.println("to address:"+ toAddress);
		System.out.println("Verify URL:"+ verifyURL);
		
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model model) {
		boolean verified = customerService.verify(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/customers/search")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/customers/search/page/{pageNum}")
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
		
		return "customers/search";
	}
	/*
	@GetMapping("/customers/review_form")
	public String newCustomerReview(Model model) {
		Review review = new Review();
		review.setEnabled(false);
		List<User> listProfessionals = professionalService.listAll();
		List<User> listCustomers = customerService.listAll();
		model.addAttribute("review",review);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("pageTitle", "Create new Review");
		return "customers/review_form";
	}
	
	@PostMapping("/reviews/save")
	public String saveCustomerReview(Review review, RedirectAttributes redirectAttributes) {
		System.out.println(review);
		service.save(review);
	
		redirectAttributes.addFlashAttribute("message", "The Review has been saved successfully.");	
		return "redirect:/customers/review_form";
	}

	*/
}
