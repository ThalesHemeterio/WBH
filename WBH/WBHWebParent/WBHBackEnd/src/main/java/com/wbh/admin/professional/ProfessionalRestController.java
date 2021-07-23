package com.wbh.admin.professional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfessionalRestController {

	@Autowired private ProfessionalService service;
	
	@PostMapping("/professionals/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		return service.isEmailUnique(id,email) ? "OK" : "Duplicated";
	}
}
