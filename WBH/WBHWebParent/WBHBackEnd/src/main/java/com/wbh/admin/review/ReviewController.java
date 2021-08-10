package com.wbh.admin.review;

import java.io.IOException;
import java.util.ArrayList;
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
import com.wbh.admin.session.SessionNotFoundException;
import com.wbh.admin.session.SessionRepository;
import com.wbh.admin.session.SessionService;
import com.wbh.admin.user.RoleRepository;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Review;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;

@Controller
public class ReviewController {
	
	@Autowired ReviewService service;
	@Autowired ProfessionalService serviceP;
	@Autowired CustomerService serviceC;
	@Autowired RoleRepository roleRepo;
	@Autowired SessionService sessionService;

	
	/*********ADMIN APPLICATION SIDE*********/
	
	// display all reviews Admin user with pagination
	@GetMapping("/admin/reviews")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/admin/reviews/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Review> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Review> listReviews = page.getContent();
		
		long startCount =(pageNum-1)*ReviewService.USERS_PER_PAGE+1;
		long endCount = startCount+ReviewService.USERS_PER_PAGE-1;
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
		model.addAttribute("listReviews",listReviews);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "admin/reviews/reviews";
	}
	
	//creating new review Admin user
	@GetMapping("/admin/reviews/new")
	public String newReview(Model model) {
		Review review = new Review();
		review.setEnabled(false);
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

		model.addAttribute("review",review);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("pageTitle", "Create new Review");
		return "admin/reviews/review_form";
	}
	
	//saves the new review by Admin user
	@PostMapping("/admin/reviews/save")
	public String saveReview(Review review, RedirectAttributes redirectAttributes) {
		System.out.println(review);
		service.save(review);
	
		redirectAttributes.addFlashAttribute("message", "The Review has been saved successfully.");	
		return getRedirectURLtoAffectedReview(review);
	}

	//redirects to all reviews page but filtering the new review done by Admin user
	private String getRedirectURLtoAffectedReview(Review review) {
		String name = review.getTitle();
		return "redirect:/admin/reviews/page/1?sortField=id&sortDir=asc&keyword="+ name;
	}
	
	//edits a review Admin User
	@GetMapping("/admin/review/edit/{id}")
	public String editReview(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Review review = service.get(id);
			List<User> listProfessionals = serviceP.listAll();
			List<User> listCustomers = serviceC.listAll();

			model.addAttribute("listProfessionals",listProfessionals);
			model.addAttribute("listCustomers",listCustomers);
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", "Edit Review (ID: "+ id+")");
			return "admin/reviews/review_form";
		} catch(ReviewNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/reviews";
	}
	
	// Admin user deletes a review 
	@GetMapping("/admin/review/delete/{id}")
	public String deleteReview(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The review "+ id + " has been deleted successfully");
		} catch(ReviewNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/admin/reviews";
	}
	
	//Admin user change enabled status of a review - CONTENT MODERATION
	@GetMapping("/admin/reviews/{id}/enabled/{status}")
	public String updateReviewEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateReviewEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Session Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/reviews";
	}
	
	
	/*********CLIENT(CUSTOMER) APPLICATION SIDE*********/
	
	//list all the reviews by an User Client(Customer)
	@GetMapping("/customers/reviews_customer")
	public String listfirstPageReviews(@AuthenticationPrincipal WBHUserDetails loggedUser,Model model) {
		return listfirstPageReviews(loggedUser,1,model, "id", "desc",null);
	}
	
	@GetMapping("/customers/reviews_customer/page/{pageNum}")
	public String listfirstPageReviews(@AuthenticationPrincipal WBHUserDetails loggedUser,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,@Param("keyword") String keyword) {
		String email = loggedUser.getUsername();
		User user = serviceP.getByEmail(email);
		model.addAttribute("user", user);
		Page<Review> page = service.listCustomerReviwesByPage(pageNum, sortField, sortDir,user);		
		List<Review> listReviews = page.getContent();
		long startCount =(pageNum-1)*ReviewService.USERS_PER_PAGE+1;
		long endCount = startCount+ReviewService.USERS_PER_PAGE-1;
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
		model.addAttribute("listReviews",listReviews);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "customers/reviews_customer";
	}
	
	//edit a review by an User Client(Customer)
	@GetMapping("/customers/review/edit/{id}")
	public String editReviewCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Review review = service.get(id);
			User listProfessionals = review.getProfessional();
			User listCustomers = review.getCustomer();

			model.addAttribute("listProfessionals",listProfessionals);
			model.addAttribute("listCustomers",listCustomers);
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", "Edit Review (ID: "+ id+")");
			return "customers/review_form";
		} catch(ReviewNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers/reviews_customer";
	}
	
	//delete a review by an User Client(Customer)
	@GetMapping("/customers/review/delete/{id}")
	public String deleteReviewCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The review "+ id + " has been deleted successfully");
		} catch(ReviewNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers/reviews_customer";
	}
	
	//save a review by an User Client(Customer)
	@PostMapping("/customers/reviews/save")
	public String saveReviewCustomer(Review review, RedirectAttributes redirectAttributes) {

		service.save(review);
	
		redirectAttributes.addFlashAttribute("message", "The Review has been saved successfully.");	
		return "redirect:/customers/reviews_customer";
	}

	//list all the reviews of a User Professional to a User Client(Customer)
	@GetMapping("/customers/reviews_professional/{id}")
	public String listfirstPageReviewsCustomer(@PathVariable(name="id") Integer id,Model model) throws ProfessionalNotFoundException {
		User user = serviceP.get(id);
		model.addAttribute("user", user);
		return listfirstPageReviewsCustomer(user,1,model, "id", "desc",null);
	}
	
	//gets all reviews of a professional to an user client
	@GetMapping("/customers/reviews_professional/page/{pageNum}")
	public String listfirstPageReviewsCustomer(User user,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Review> page = service.listReviewsByProfessional(user,pageNum, sortField, sortDir,keyword);
		List<Review> listReviews = page.getContent();

		long startCount =(pageNum-1)*ReviewService.USERS_PER_PAGE+1;
		long endCount = startCount+ReviewService.USERS_PER_PAGE-1;
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
		model.addAttribute("listReviews",listReviews);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("professional", user.getFullName());
		
		return "customers/reviews_professional";
	}
	
	//creates a new review by an User Client(Customer)
	@GetMapping("/customers/session/review/{id}")
	public String newReviewCustomer(@PathVariable(name="id") Integer id,Model model) throws SessionNotFoundException {
		Review review = new Review();
		review.setEnabled(false);
	
		Session session = sessionService.get(id);
		User listProfessionals = session.getProfessional();
		User listCustomers = session.getCustomer();

		model.addAttribute("review",review);
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("pageTitle", "Create new Review");
		return "customers/review_form";
	}
	
	/*********PROFESSIONALS APPLICATION SIDE*********/
	
	//list all reviews of an User Professional
	@GetMapping("/professionals/reviews_professional/{id}")
	public String listfirstPageReviewsProfessional(@PathVariable(name="id") Integer id,Model model) throws ProfessionalNotFoundException {
		User user = serviceP.get(id);
		model.addAttribute("user", user);
		return listfirstPageReviewsProfessional(id,1,model, "id", "desc",null);
	}
	
	@GetMapping("/professionals/reviews_professional/page/{id}/{pageNum}")
	public String listfirstPageReviewsProfessional(@PathVariable(name="id") Integer id,
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) throws ProfessionalNotFoundException {
		User user = serviceP.get(id);
		Page<Review> page = service.listReviewsByProfessional(user,pageNum, sortField, sortDir,keyword);
		List<Review> listAllReviews = page.getContent();
		List<Review> listReviews = new ArrayList<Review>();
		for(Review review : listAllReviews) {	
			if((review.getProfessional() == user && review.isEnabled())) {
				listReviews.add(review);
			}
		}
		long startCount =(pageNum-1)*ReviewService.USERS_PER_PAGE+1;
		long endCount = startCount+ReviewService.USERS_PER_PAGE-1;
		if(endCount >page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		String pageTitle = user.getFullName();
		//passing the attributes to the view
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("listReviews",listReviews);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageTitle",pageTitle);
		
		return "professionals/reviews_professional";
	}

}


