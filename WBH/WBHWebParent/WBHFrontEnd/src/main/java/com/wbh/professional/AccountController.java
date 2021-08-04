package com.wbh.professional;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.FileUploadUtil;
import com.wbh.user.UserService;
import com.wbh.common.entity.User;
import com.wbh.security.CustomerUserDetails;
// class for editing user account when logged
@Controller
public class AccountController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/professionals/account")
	public String viewDetailsProfessional(@AuthenticationPrincipal CustomerUserDetails loggedUser, Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		model.addAttribute("user", user);
		
		return "professionals/account_form";
	}
	
	@GetMapping("/customers/account")
	public String viewDetailsCustomer(@AuthenticationPrincipal CustomerUserDetails loggedUser, Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		model.addAttribute("user", user);
		
		return "customers/account_form";
	}

	@PostMapping("/account/customer/update")
	public String saveDetailsCustomer(User user, RedirectAttributes redirectAttributes, 
			@AuthenticationPrincipal CustomerUserDetails loggedUser,
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {

		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);
			
			String uploadDir = "user-photos/"+savedUser.getId();
			
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.updateAccount(user);
		}
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");	
		return "redirect:/customers/account";
	}
	
	@PostMapping("/account/professional/update")
	public String saveDetailsProfessional(User user, RedirectAttributes redirectAttributes, 
			@AuthenticationPrincipal CustomerUserDetails loggedUser,
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {

		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);
			
			String uploadDir = "user-photos/"+savedUser.getId();
			
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.updateAccount(user);
		}
		loggedUser.setFirstName(user.getFirstName());
		loggedUser.setLastName(user.getLastName());
		redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");	
		return "redirect:/professionals/account";
	}
}
