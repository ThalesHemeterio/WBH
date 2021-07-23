package com.wbh.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.wbh.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired private CustomerService service;
	
	@GetMapping("/register_customer")
	public String showRegistrationCustomerForm(Model model) {
		model.addAttribute("pageTitle","Client Registration");
		model.addAttribute("customer", new Customer());
		
		return "register/register_customer_form";
	}
	
}
