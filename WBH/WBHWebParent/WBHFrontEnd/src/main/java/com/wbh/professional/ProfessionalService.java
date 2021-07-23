package com.wbh.professional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;

@Service
public class ProfessionalService {

	
	@Autowired private ProfessionalRepository professionalRepo;
	
	public boolean isEmailUnique(String email) {
		Professional professional = professionalRepo.findByEmail(email);
		return professional ==null;
	}
}
