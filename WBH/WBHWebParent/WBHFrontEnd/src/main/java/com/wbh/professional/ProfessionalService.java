package com.wbh.professional;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.User;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class ProfessionalService {

	
	@Autowired private ProfessionalRepository professionalRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public List<User> listAll(){
		return (List<User>) professionalRepo.findAll();
	}
	
	public boolean isEmailUnique(String email) {
		User professional = professionalRepo.findByEmail(email);
		return professional ==null;
	}
	
	public void registerProfessional(User professional) {
		encodePassword(professional);
		professional.setEnabled(false);
		professional.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		professional.setVerificationCode(randomCode);
		
		professionalRepo.save(professional);
	}

	private void encodePassword(User professional) {
		String encodedPassword = passwordEncoder.encode(professional.getPassword());
		professional.setPassword(encodedPassword);
	}
	
	public boolean verify(String verificationCode) {
		User professional = professionalRepo.findByVerificationCode(verificationCode);
		if(professional == null || professional.isEnabled()) {
			return false;
		}else {
			professionalRepo.verify(professional.getId());
			return true;
		}
	}
}
