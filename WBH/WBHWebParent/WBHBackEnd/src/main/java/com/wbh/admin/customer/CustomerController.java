package com.wbh.admin.customer;

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
import com.wbh.common.entity.Customer;


@Controller
public class CustomerController {

	@Autowired
	private CustomerService service;
	
	@GetMapping("/customers")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "firstName", "asc",null);
	}
	
	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Customer> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Customer> listCustomers = page.getContent();
		
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
		model.addAttribute("listCustomers",listCustomers);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/new")
	public String newCustomer(Model model) {
		Customer customer = new Customer();
		customer.setEnabled(true);
		model.addAttribute("customer",customer);
		model.addAttribute("pageTitle", "Create new Client");
		return "customers/customer_form";
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {
		System.out.println(customer);
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			customer.setPhotos(fileName);
			Customer savedUser = service.save(customer);
			String uploadDir = "customer-photos/"+savedUser.getId();
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(customer.getPhotos().isEmpty()) customer.setPhotos(null);
			service.save(customer);
		}
		redirectAttributes.addFlashAttribute("message", "The Client has been saved successfully.");	
		return getRedirectURLtoAffectedCustomer(customer);
	}

	private String getRedirectURLtoAffectedCustomer(Customer customer) {
		String firstPartOfEmail = customer.getEmail().split("@")[0];
		return "redirect:/customers/page/1?sortField=id&sortDir=asc&keyword="+ firstPartOfEmail;
	}
	
	@GetMapping("/customer/edit/{id}")
	public String editCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = service.get(id);
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", "Edit Client (ID: "+ id+")");
			return "customers/customer_form";
		} catch(CustomerNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers";
	}
	
	@GetMapping("/customer/delete/{id}")
	public String deleteCustomer(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The client "+ id + " has been deleted successfully");
		} catch(CustomerNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCustomerEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Client Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/customers";
	}
}
