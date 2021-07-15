package com.wbh.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

@Controller
public class UserController {

		@Autowired
		private UserService service;
		
		@GetMapping("/users")
		public String listfirstPage(Model model) {
			return listByPage(1,model, "firstName", "asc");
		}
		
		@GetMapping("/users/page/{pageNum}")
		public String listByPage(@PathVariable(name="pageNum") int pageNum, Model model, 
					@Param("sortField") String sortField, @Param("sortDir") String sortDir) {
			Page<User> page = service.listByPage(pageNum, sortField, sortDir);
			List<User> listUsers = page.getContent();
			
			long startCount =(pageNum-1)*UserService.USERS_PER_PAGE+1;
			long endCount = startCount+UserService.USERS_PER_PAGE-1;
			if(endCount >page.getTotalElements()) {
				endCount = page.getTotalElements();
			}
			
			String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems",page.getTotalElements());
			model.addAttribute("listUsers",listUsers);
			model.addAttribute("sortField",sortField);
			model.addAttribute("sortDir",sortDir);
			model.addAttribute("reverseSortDir",reverseSortDir);
	
			return "users";
		}
		
		@GetMapping("/users/new")
		public String newUser(Model model) {
			List<Role> listRoles = service.listRoles();
			User user = new User();
			user.setEnabled(true);
			model.addAttribute("user",user);
			model.addAttribute("listRoles",listRoles);
			model.addAttribute("pageTitle", "Create new User");
			return "user_form";
		}
		
		@PostMapping("/users/save")
		public String saveUser(User user, RedirectAttributes redirectAttributes) {
			System.out.println(user);
			service.save(user);
			
			redirectAttributes.addFlashAttribute("message", "The User has been saved successfully.");
			return "redirect:/users";
		}
		
		@GetMapping("/user/edit/{id}")
		public String editUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
			try {
				User user = service.get(id);
				List<Role> listRoles = service.listRoles();
				model.addAttribute("user", user);
				model.addAttribute("pageTitle", "Edit User (ID: "+ id+")");
				model.addAttribute("listRoles",listRoles);
				return "user_form";
			} catch(UserNotFoundException ex){
				redirectAttributes.addFlashAttribute("message", ex.getMessage());
			}
			return "redirect:/users";
		}
		
		@GetMapping("/user/delete/{id}")
		public String deleteUser(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
			try {
				service.delete(id);
				redirectAttributes.addFlashAttribute("message", "The user ID "+ id + " has been deleted successfully");
			} catch(UserNotFoundException ex){
				redirectAttributes.addFlashAttribute("message", ex.getMessage());
			}
			return "redirect:/users";
		}
		
		@GetMapping("/users/{id}/enabled/{status}")
		public String updateUserEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
			service.updateUserEnabledStatus(id, enabled);
			String status = enabled ?"enabled" :"disabled";
			String message ="The user Id " + id + " has been " + status;
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/users";
		}
		
}