package com.wbh.professional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfessionalRestController {

	@Autowired private ProfessionalService service;
	
	@PostMapping("/professionals/check_unique_email")
	public String checkDplicateEmail(@Param("email") String email) {
		return service.isEmailUnique(email) ? "OK" : "Duplicated";
	}
}
