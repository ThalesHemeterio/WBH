package com.wbh.admin.session;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.admin.FileUploadUtil;
import com.wbh.admin.category.CategoryService;
import com.wbh.admin.customer.CustomerService;
import com.wbh.admin.professional.ProfessionalNotFoundException;
import com.wbh.admin.professional.ProfessionalService;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.common.entity.Session;

@Controller
public class SessionController {
	
	@Autowired SessionService service;
	@Autowired ProfessionalService serviceP;
	@Autowired CustomerService serviceC;
	@Autowired CategoryService serviceCat;
	
	@GetMapping("/sessions")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/sessions/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
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
		
		return "sessions/sessions";
	}
	
	@GetMapping("/sessions/new")
	public String newSession(Model model) {
		Session session = new Session();
		session.setEnabled(false);
		List<Professional> listProfessionals = serviceP.listAll();
		List<Customer> listCustomers = serviceC.listAll();
		List<Category> listCategories = serviceCat.listAll();
		model.addAttribute("session",session);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("pageTitle", "Create new Session");
		return "sessions/session_form";
	}
	
	@PostMapping("/sessions/save")
	public String saveSession(Session session, RedirectAttributes redirectAttributes) {
		System.out.println(session);
		service.save(session);
	
		redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully.");	
		return getRedirectURLtoAffectedSession(session);
	}

	private String getRedirectURLtoAffectedSession(Session session) {
		String name = session.getName();
		return "redirect:/sessions/page/1?sortField=id&sortDir=asc&keyword="+ name;
	}
	
	@GetMapping("/session/edit/{id}")
	public String editSession(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Session session = service.get(id);
			List<Professional> listProfessionals = serviceP.listAll();
			List<Customer> listCustomers = serviceC.listAll();
			List<Category> listCategories = serviceCat.listAll();
			model.addAttribute("listProfessionals",listProfessionals);
			model.addAttribute("listCustomers",listCustomers);
			model.addAttribute("listCategories",listCategories);
			
			model.addAttribute("session", session);
			model.addAttribute("pageTitle", "Edit Session (ID: "+ id+")");
			return "sessions/session_form";
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/sessions";
	}
	
	@GetMapping("/session/delete/{id}")
	public String deleteSession(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The session "+ id + " has been deleted successfully");
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/sessions";
	}
	
	@GetMapping("/sessions/{id}/enabled/{status}")
	public String updateSessionEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateSessionEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Session Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/sessions";
	}
}


