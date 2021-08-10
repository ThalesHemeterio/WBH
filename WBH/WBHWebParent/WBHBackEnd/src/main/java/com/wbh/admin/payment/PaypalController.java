package com.wbh.admin.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.wbh.admin.session.SessionNotFoundException;
import com.wbh.admin.session.SessionService;
import com.wbh.common.entity.Session;

@Controller
public class PaypalController {

	@Autowired PaypalService paypalservice;
	@Autowired SessionService sessionService;
	
	@GetMapping("/customers/search/booking/pay/{id}")
	public String payment(@PathVariable(name="id") Integer id) throws SessionNotFoundException {
		try {
			Session session = sessionService.get(id);
			Double price = 50.00;
			Payment payment = paypalservice.createPayment(price, "EUR", "Paypal", "Sale",
					"description", "http://localhost:8080/WBH/customers/search/booking/pay/"+session.getId()+"/cancel", "http://localhost:8080/WBH/customers/search/booking/pay/"+session.getId()+"/success");
			for(Links link:payment.getLinks()) {
				if(link.getRel().equals("approval_url")) {
					return "redirect:"+link.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return "redirect:/customers/search";
	}
	
	@GetMapping("customers/search/booking/pay/{id}/cancel")
	public String cancelPay(@PathVariable(name="id") Integer id,RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("message", "The booking/payment was not efectuated");
		return "redirect:/customers/search";
	}
	
	@GetMapping("customers/search/booking/pay/{id}/success")
	public String successPay(@RequestParam("paymentId") String paymentID, @RequestParam("PayerID") String payerID,RedirectAttributes redirectAttributes) {
		try {
			Payment payment = paypalservice.executePayment(paymentID, payerID);
			System.out.println(payment.toJSON());
			if(payment.getState().equals("approved")) {
				redirectAttributes.addFlashAttribute("message", "The booking/payment has been efectuated successfully");
				return "redirect:/customers/search/booking/{id}";
			}
		}catch (PayPalRESTException e) {
			System.out.println(e.getMessage());
		}	
		return "redirect:/customers/search";
	}	
	
}
