package com.wbh.admin.review;

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
	
	@PostMapping("/admin/reviews/save")
	public String saveReview(Review review, RedirectAttributes redirectAttributes) {
		System.out.println(review);
		service.save(review);
	
		redirectAttributes.addFlashAttribute("message", "The Review has been saved successfully.");	
		return getRedirectURLtoAffectedReview(review);
	}

	private String getRedirectURLtoAffectedReview(Review review) {
		String name = review.getTitle();
		return "redirect:/admin/reviews/page/1?sortField=id&sortDir=asc&keyword="+ name;
	}
	
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
	
	@GetMapping("/admin/reviews/{id}/enabled/{status}")
	public String updateReviewEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateReviewEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Session Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/reviews";
	}
	
	
	@GetMapping("/customers/reviews_customer")
	public String listfirstPageReviews(Model model) {
		return listfirstPageReviews(1,model, "id", "desc",null);
	}
	
	@GetMapping("/customers/reviews_customer/page/{pageNum}")
	public String listfirstPageReviews(
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
		
		return "customers/reviews_customer";
	}
	

}


