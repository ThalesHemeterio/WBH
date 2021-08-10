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
	
	/********** UNREGISTERED USER SECTION **********/
	
	//displays search page to unregistred users
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
	
	/********** ADMIN USER SECTION **********/
	
	//list all sessions to Admin user
	@GetMapping("/admin/sessions")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	//list all sessions to Admin user using pagination
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
	
	//creates new session by Admin user
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
	
	//saves a new session created by Admin user
	@PostMapping("/admin/sessions/save")
	public String saveSession(Session session, RedirectAttributes redirectAttributes) {
		System.out.println(session);
		service.save(session);
	
		redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully.");	
		return getRedirectURLtoAffectedSession(session);
	}

	//redirects to all sessions page filtering by the new session created
	private String getRedirectURLtoAffectedSession(Session session) {
		String name = session.getName();
		return "redirect:/admin/sessions/page/1?sortField=id&sortDir=asc&keyword="+ name;
	}
	
	//edits a session - Admin user
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
	
	//deletes a session - Admin user
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
	
	//updates session status - Admin user
	@GetMapping("/admin/sessions/{id}/enabled/{status}")
	public String updateSessionEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateSessionEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Session Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/sessions";
	}
	
	/********** PROFESSIONAL USER SECTION **********/
	
	//deletes a session - professional user
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
	
	//get all the sessions
	@GetMapping("/professionals/sessions")
	public String listByPageSearchProfessionals(@AuthenticationPrincipal WBHUserDetails loggedUser,Model model) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		model.addAttribute("user", user);
		return listfirstPageSearchProfessionals(loggedUser,1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/sessions/page/{pageNum}")
	public String listfirstPageSearchProfessionals(@AuthenticationPrincipal WBHUserDetails loggedUser,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		Page<Session> page = service.listByPageByProfessional(pageNum, sortField, sortDir,user,keyword);
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
	
	//professionals search dosplaying only sessions not booked
	@GetMapping("/professionals/search")
	public String listfirstPageSearchProfessional(Model model) {
		return listByPageSearchProfessional(1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/search/page/{pageNum}")
	public String listByPageSearchProfessional(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPageAndCustomer(pageNum, sortField, sortDir,keyword);
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
	
	//creates e new session by professional user
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
	
	//save new session by porfessional user
	@PostMapping("/professionals/session_form/save")
	public String saveSessionprofessional(Session session, RedirectAttributes redirectAttributes) {
		System.out.println(session);
		session.setEnabled(true);
		service.save(session);
		redirectAttributes.addFlashAttribute("message", "The Session has been saved successfully.");	
		return "redirect:/professionals/sessions";
	}
	
	//edit session by professional user
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
	
	/********** CUSTOMER USER SECTION **********/
	
	//gets all the sessions book by the customer user
	@GetMapping("/customers/sessions")
	public String listfirstPageSearchCustomers(@AuthenticationPrincipal WBHUserDetails loggedUser,Model model) {
		return listByPageSearchCustomers(loggedUser,1,model, "id", "desc",null);
	}
	
	//gets all the sessions book by the customer user with pagination
	@GetMapping("/customers/sessions/page/{pageNum}")
	public String listByPageSearchCustomers(@AuthenticationPrincipal WBHUserDetails loggedUser,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		model.addAttribute("user", user);
		Page<Session> page = service.listByPageAndCustomerBooked(user, pageNum, sortField, sortDir,keyword);
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
		
		return "customers/sessions_customer";
	}
	
	//search for user customers
	@GetMapping("/customers/search")
	public String listfirstPageSearchCustomer(Model model) {
		return listByPageSearchCustomer(1,model, "id", "desc",null);
	}
	//search for user customers with pagination
	@GetMapping("/customers/search/page/{pageNum}")
	public String listByPageSearchCustomer(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPageAndCustomer(pageNum, sortField, sortDir,keyword);
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
		
		return "customers/search";
	}
	
	//cancel booking by customer user
	@GetMapping("/customers/search/cancelbooking/{id}")
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
	
	//booning session by customer user
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


