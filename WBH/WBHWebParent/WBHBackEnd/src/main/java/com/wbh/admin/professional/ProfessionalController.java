package com.wbh.admin.professional;

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
import com.wbh.admin.setting.EmailSettingBag;
import com.wbh.admin.setting.SettingService;


@Controller
public class ProfessionalController {

	@Autowired
	private ProfessionalService service;
	@Autowired private SettingService settingService;
	
	@Autowired RoleRepository roleRepo;
	
	@GetMapping("/admin/professionals")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "firstName", "asc",null);
	}
	
	@GetMapping("/admin/professionals/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<User> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<User> listProfessionals = page.getContent();
		Role roleProfessional = roleRepo.getRoleById(3);
		long startCount =(pageNum-1)*ProfessionalService.USERS_PER_PAGE+1;
		long endCount = startCount+ProfessionalService.USERS_PER_PAGE-1;
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
		model.addAttribute("roleProfessional",roleProfessional);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "admin/professionals/professionals";
	}
	
	@GetMapping("/admin/professionals/new")
	public String newCustomer(Model model) {
		Role listRoles = roleRepo.getRoleById(3);
		User professional = new User();
		professional.setEnabled(false);
		model.addAttribute("professional",professional);
		model.addAttribute("listRoles",listRoles);
		model.addAttribute("pageTitle", "Create new Professional");
		return "admin/professionals/professional_form";
	}
	
	@PostMapping("/admin/professionals/save")
	public String saveProfessional(User professional, RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {
		System.out.println(professional);
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			professional.setPhotos(fileName);
			User savedProfessional = service.save(professional);
			String uploadDir = "user-photos/"+savedProfessional.getId();
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(professional.getPhotos().isEmpty()) professional.setPhotos(null);
			service.save(professional);
		}
		redirectAttributes.addFlashAttribute("message", "The Professional has been saved successfully.");	
		return getRedirectURLtoAffectedProfessional(professional);
	}

	private String getRedirectURLtoAffectedProfessional(User professional) {
		String firstPartOfEmail = professional.getEmail().split("@")[0];
		return "redirect:/admin/professionals/page/1?sortField=id&sortDir=asc&keyword="+ firstPartOfEmail;
	}
	
	@GetMapping("/admin/professional/edit/{id}")
	public String editProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			User professional = service.get(id);
			Role listRoles = roleRepo.getRoleById(3);
			model.addAttribute("professional", professional);
			model.addAttribute("pageTitle", "Edit Professional (ID: "+ id+")");
			model.addAttribute("listRoles",listRoles);
			return "admin/professionals/professional_form";
		} catch(ProfessionalNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/professionals";
	}
	
	@GetMapping("/admin/professional/delete/{id}")
	public String deleteProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The professional "+ id + " has been deleted successfully");
		} catch(ProfessionalNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/professionals";
	}
	
	@GetMapping("/admin/professionals/{id}/enabled/{status}")
	public String updateProfessionalEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateProfessionalEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Professional Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/professionals";
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
		boolean isUnique = service.isEmailUnique(professional.getId(), professional.getEmail());
		if(isUnique) {
		service.registerProfessional(professional);
		Role Role = roleRepo.getRoleById(2);
		professional.addRole(Role);
		sendVerificationEmail(request,professional);
		
		model.addAttribute("pageTitle","Registration Succeeded");
		return "/register/register_success";
		}else {
			model.addAttribute("pageTitle","Registration Failed");
			return "redirect:register/register_professional";
		}
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
}
