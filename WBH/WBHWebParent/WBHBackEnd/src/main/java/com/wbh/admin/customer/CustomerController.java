package com.wbh.admin.customer;

import java.io.IOException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.admin.Utility;
import com.wbh.admin.FileUploadUtil;
import com.wbh.admin.user.RoleRepository;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;
import com.wbh.admin.setting.SettingService;
import com.wbh.admin.setting.EmailSettingBag;


@Controller
public class CustomerController {

	@Autowired
	private CustomerService service;
	@Autowired private SettingService settingService;
	
	@Autowired RoleRepository roleRepo;
	
	@GetMapping("/admin/customers")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "firstName", "asc",null);
	}
	
	@GetMapping("/admin/customers/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<User> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<User> listCustomers = page.getContent();
		Role roleCustomer = roleRepo.getRoleById(2);
		long startCount =(pageNum-1)*CustomerService.USERS_PER_PAGE+1;
		long endCount = startCount+CustomerService.USERS_PER_PAGE-1;
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
		model.addAttribute("roleCustomer",roleCustomer);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "admin/customers/customers";
	}
	
	@GetMapping("/admin/customers/new")
	public String newCustomer(Model model) {
		Role listRoles = roleRepo.getRoleById(2);
		User customer = new User();
		customer.setEnabled(true);
		model.addAttribute("customer",customer);
		model.addAttribute("listRoles",listRoles);
		model.addAttribute("pageTitle", "Create new Client");
		return "admin/customers/customer_form";
	}
	
	@PostMapping("/admin/customers/save")
	public String saveCustomer(User customer, RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {
		System.out.println(customer);
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			customer.setPhotos(fileName);
			User savedUser = service.save(customer);
			String uploadDir = "user-photos/"+savedUser.getId();
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(customer.getPhotos().isEmpty()) customer.setPhotos(null);
			service.save(customer);
		}
		redirectAttributes.addFlashAttribute("message", "The Client has been saved successfully.");	
		return getRedirectURLtoAffectedCustomer(customer);
	}

	private String getRedirectURLtoAffectedCustomer(User customer) {
		String firstPartOfEmail = customer.getEmail().split("@")[0];
		return "redirect:/admin/customers/page/1?sortField=id&sortDir=asc&keyword="+ firstPartOfEmail;
	}
	
	@GetMapping("/admin/customer/edit/{id}")
	public String editCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			User customer = service.get(id);
			Role listRoles = roleRepo.getRoleById(2);
			model.addAttribute("listRoles",listRoles);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", "Edit Client (ID: "+ id+")");
			return "admin/customers/customer_form";
		} catch(CustomerNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/customers";
	}
	
	@GetMapping("/admin/customer/delete/{id}")
	public String deleteCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The client "+ id + " has been deleted successfully");
		} catch(CustomerNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/customers";
	}
	
	@GetMapping("/admin/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Client Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/customers";
	}
	
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
		boolean isUnique = service.isEmailUnique(customer.getId(), customer.getEmail());
		if(isUnique) {
		service.registerCustomer(customer);
		Role Role = roleRepo.getRoleById(2);
		customer.addRole(Role);
		sendVerificationEmail(request,customer);
		
		model.addAttribute("pageTitle","Registration Succeeded");
		
		return "/register/register_success";
		}else {
			model.addAttribute("pageTitle","Registration Failed");
			return "redirect:register/register_customer";
		}
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
		boolean verified = service.verify(code);
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
}
