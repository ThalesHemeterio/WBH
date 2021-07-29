package com.wbh.professional;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class ProfessionalService {

	
	@Autowired private ProfessionalRepository professionalRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public boolean isEmailUnique(String email) {
		Professional professional = professionalRepo.findByEmail(email);
		return professional ==null;
	}
	
	public void registerProfessional(Professional professional) {
		encodePassword(professional);
		professional.setEnabled(false);
		professional.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		professional.setVerificationCode(randomCode);
		
		professionalRepo.save(professional);
	}

	private void encodePassword(Professional professional) {
		String encodedPassword = passwordEncoder.encode(professional.getPassword());
		professional.setPassword(encodedPassword);
	}
	
	public boolean verify(String verificationCode) {
		Professional professional = professionalRepo.findByVerificationCode(verificationCode);
		if(professional == null || professional.isEnabled()) {
			return false;
		}else {
			professionalRepo.verify(professional.getId());
			return true;
		}
	}
}
