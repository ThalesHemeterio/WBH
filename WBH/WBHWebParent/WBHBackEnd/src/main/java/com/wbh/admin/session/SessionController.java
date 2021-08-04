package com.wbh.admin.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.wbh.admin.security.WBHUserDetails;
import com.wbh.admin.user.RoleRepository;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;

@Controller
public class SessionController {
	
	@Autowired SessionService service;
	@Autowired ProfessionalService serviceP;
	@Autowired CustomerService serviceC;
	@Autowired CategoryService serviceCat;
	@Autowired RoleRepository roleRepo;
	
	@GetMapping("/admin/sessions")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/search")
	public String listfirstPageSearch(Model model) {
		return listfirstPageSearch(1,model, "id", "desc",null);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String listfirstPageSearch(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listSessions = page.getContent();
		List<User> listProfessionals = serviceP.listAll();
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
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listSessions",listSessions);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "search";
	}
	
	@GetMapping("/admin/sessions/page/{pageNum}")
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
		
		return "admin/sessions/sessions";
	}
	
	@GetMapping("/admin/sessions/new")
	public String newSession(Model model) {
		Session session = new Session();
		session.setEnabled(false);
		List<User> listUsers = serviceP.listAll();
		List<User> listProfessionals = serviceP.listAll();
		listProfessionals.clear();
		Role roleProfessional = roleRepo.getRoleById(3);/// passing only the user professionals
		for(User user : listUsers) {	
			if((user.getFirstRole()==roleProfessional)) {
				listProfessionals.add(user);
			}
		}
		Role roleCustomer = roleRepo.getRoleById(2); /// passing only the user clients
		List<User> listUsers2 = serviceC.listAll();
		List<User> listCustomers = serviceP.listAll();
		listCustomers.clear();
		for(User customer : listUsers2) {	
			if((customer.getFirstRole()==roleCustomer)) {
				listCustomers.add(customer);
			}
		}
		
		List<Category> listCategories = serviceCat.listAll();
		model.addAttribute("session",session);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("pageTitle", "Create new Session");
		return "admin/sessions/session_form";
	}
	
	@PostMapping("/admin/sessions/save")
	public String saveSession(Session session, RedirectAttributes redirectAttributes) {
		System.out.println(session);
		service.save(session);
	
		redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully.");	
		return getRedirectURLtoAffectedSession(session);
	}

	private String getRedirectURLtoAffectedSession(Session session) {
		String name = session.getName();
		return "redirect:/admin/sessions/page/1?sortField=id&sortDir=asc&keyword="+ name;
	}
	
	@GetMapping("/admin/session/edit/{id}")
	public String editSession(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Session session = service.get(id);
			List<User> listProfessionals = serviceP.listAll();
			List<User> listCustomers = serviceC.listAll();
			List<Category> listCategories = serviceCat.listAll();
			model.addAttribute("listProfessionals",listProfessionals);
			model.addAttribute("listCustomers",listCustomers);
			model.addAttribute("listCategories",listCategories);
			
			model.addAttribute("session", session);
			model.addAttribute("pageTitle", "Edit Session (ID: "+ id+")");
			return "admin/sessions/session_form";
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/sessions";
	}
	
	@GetMapping("/admin/session/delete/{id}")
	public String deleteSession(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The session "+ id + " has been deleted successfully");
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/sessions";
	}
	
	@GetMapping("/professionals/session/delete/{id}")
	public String deleteSessionProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The session "+ id + " has been deleted successfully");
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/professionals/sessions";
	}
	
	@GetMapping("/admin/sessions/{id}/enabled/{status}")
	public String updateSessionEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateSessionEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Session Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/sessions";
	}
	
	@GetMapping("/professionals/sessions")
	public String listByPageSearchProfessionals(@AuthenticationPrincipal WBHUserDetails loggedUser,Model model) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		model.addAttribute("user", user);
		return listfirstPageSearchProfessionals(user,1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/sessions/page/{pageNum}")
	public String listfirstPageSearchProfessionals(User user,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		//Integer prof_id = user.getId();
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listAllSessions = page.getContent();
		List<Session> listSessions = new ArrayList<Session>();
		for(Session session : listAllSessions) {	
			if((session.getProfessional()==user)) {
				listSessions.add(session);
			}
		}
		
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
		
		return "professionals/sessions_professional";
	}
	
	//professionals search
	
	@GetMapping("/professionals/search")
	public String listfirstPageSearchProfessional(Model model) {
		return listByPageSearchProfessional(1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/search/page/{pageNum}")
	public String listByPageSearchProfessional(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listSessions = page.getContent();
		List<User> listUsers = serviceP.listAll();
		List<User> listProfessionals = serviceP.listAll();
		listProfessionals.clear();
		Role roleProfessional = roleRepo.getRoleById(3);/// passing only the user professionals
		for(User user : listUsers) {	
			if((user.getFirstRole()==roleProfessional)) {
				listProfessionals.add(user);
			}
		}
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
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listSessions",listSessions);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "professionals/search";
	}
	
	@GetMapping("/professionals/session_form")
	public String newSessionProfessional(Model model,@AuthenticationPrincipal WBHUserDetails loggedUser) {
		Session session = new Session();
		session.setEnabled(false);
		String email = loggedUser.getUsername();
		User professional = serviceP.getByEmail(email);
		List<Category> listCategories = serviceCat.listAll();
		model.addAttribute("professional",professional);
		model.addAttribute("session",session);
		model.addAttribute("listCategories",listCategories);
		model.addAttribute("pageTitle", "Create new Session");
		return "professionals/session_form";
	}
	
	@PostMapping("/professionals/session_form/save")
	public String saveSessionprofessional(Session session, RedirectAttributes redirectAttributes) {
		System.out.println(session);
		session.setEnabled(true);
		service.save(session);
		redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully.");	
		return "redirect:/professionals/sessions";
	}
	
	@GetMapping("/professionals/session/edit/{id}")
	public String editSessionProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes,@AuthenticationPrincipal WBHUserDetails loggedUser) {
		try {
			Session session = service.get(id);
			String email = loggedUser.getUsername();
			User professional = serviceP.getByEmail(email);
			List<User> listCustomers = serviceC.listAll();
			List<Category> listCategories = serviceCat.listAll();
			model.addAttribute("professional",professional);
			model.addAttribute("listCustomers",listCustomers);
			model.addAttribute("listCategories",listCategories);
			
			model.addAttribute("session", session);
			model.addAttribute("pageTitle", "Edit Session (ID: "+ id+")");
			return "professionals/session_form";
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/professionals/sessions";
	}
	
	
	//customers search
	
	@GetMapping("/customers/sessions")
	public String listfirstPageSearchCustomers(@AuthenticationPrincipal WBHUserDetails loggedUser,Model model) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		model.addAttribute("user", user);
		return listByPageSearchCustomers(user,1,model, "id", "desc",null);
	}
	
	@GetMapping("/customers/sessions/page/{pageNum}")
	public String listByPageSearchCustomers(User user,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		//Integer prof_id = user.getId();
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listAllSessions = page.getContent();
		List<Session> listSessions = new ArrayList<Session>();
		for(Session session : listAllSessions) {	
			if((session.getCustomer()== user)) {
				listSessions.add(session);
			}
		}
		
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
		
		return "customers/sessions_customer";
	}
	
	@GetMapping("/customers/search")
	public String listfirstPageSearchCustomer(Model model) {
		return listByPageSearchCustomer(1,model, "id", "desc",null);
	}
	
	@GetMapping("/customers/search/page/{pageNum}")
	public String listByPageSearchCustomer(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listAllSessions = page.getContent();
		List<Session> listSessions = new ArrayList<Session>();;
		for(Session session : listAllSessions) {	
			if(session.getCustomer()== null) {
				listSessions.add(session);
			}
		}
		
		List<User> listUsers = serviceP.listAll();
		List<User> listProfessionals = serviceP.listAll();
		listProfessionals.clear();
		Role roleProfessional = roleRepo.getRoleById(3);/// passing only the user professionals
		for(User user : listUsers) {	
			if((user.getFirstRole()==roleProfessional)) {
				listProfessionals.add(user);
			}
		}
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
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listSessions",listSessions);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "customers/search";
	}
	//cancel booking
	
	@GetMapping("/customers/session/cancelbooking/{id}")
	public String cancelBookingCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Session session = service.get(id);
			session.setCustomer(null);
			service.save(session);
			redirectAttributes.addFlashAttribute("message", "The booking "+ id + " has been canceled successfully");
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers/sessions";
	}
	
	@GetMapping("/customers/search/booking/{id}")
	public String bookingCustomer(@AuthenticationPrincipal WBHUserDetails loggedUser,@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			String email = loggedUser.getUsername();
			User user = serviceP.getByEmail(email);
			Session session = service.get(id);
			session.setCustomer(user);
			service.save(session);
		} catch(SessionNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers/sessions";
	}
}


