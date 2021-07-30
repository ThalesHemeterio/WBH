package com.wbh.professional;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Professional;

@Service
@Transactional
public class ProfessionalService {

	
	@Autowired private ProfessionalRepository professionalRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public boolean isEmailUnique(String email) {
		Professional professional = professionalRepo.findByEmail(email);
		return professional ==null;
	}
	
	public Professional getByEmail(String email) {
		return professionalRepo.findByEmail(email);
	}
	
	

	private void encodePassword(Professional professional) {
		String encodedPassword = passwordEncoder.encode(professional.getPassword());
		professional.setPassword(encodedPassword);
	}
	
	
	public Professional updateAccount(Professional userInForm) {     // updates an account of a logged user
		Professional userInDB = professionalRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		if(userInForm.getPhotos() !=null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		return professionalRepo.save(userInDB);
	}
}
