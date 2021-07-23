package com.wbh.admin.professional;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;

@Service
@Transactional
public class ProfessionalService {

	public static final int USERS_PER_PAGE =4;
	
	@Autowired 
	private ProfessionalRepository professionalRepo;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	public Professional getByEmail(String email) {
		return professionalRepo.getProfessionalByEmail(email);
	}
	
	public List<Professional> listAll(){
		return (List<Professional>) professionalRepo.findAll();
	}
	
	public Page<Professional> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);	
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return professionalRepo.findAll(keyword, pageable);
		}
		return professionalRepo.findAll(pageable);
	}
	

	public Professional save(Professional professional) {
		boolean isUpdatingProfessional = (professional.getId()!=null);								
		
		if(isUpdatingProfessional) {														// checking if you're are editing or creating a user if the same email.
			Professional existingProfessional = professionalRepo.findById(professional.getId()).get();
			
			if(professional.getPassword().isEmpty()) {                                      // password is not required in changing you can keep the same just not fill the field
				professional.setPassword(existingProfessional.getPassword());                
			}else {
				encodePassword(professional);
			}
		}else {
			encodePassword(professional);
		}
		return professionalRepo.save(professional);
	}
	
	public void encodePassword(Professional professional) {											// Function responsible for encoding the password 		
		String encodedPassowrd = passwordEncoder.encode(professional.getPassword());
		professional.setPassword(encodedPassowrd);
		
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Professional professionalByEmail = professionalRepo.getProfessionalByEmail(email);
		
		if(professionalByEmail == null) return true;
		
		boolean isCreatingNew = (id==null);
		
		if(isCreatingNew) {                                   // checking if you're are editing or creating a user if the same email.
			if(professionalByEmail !=null) return false;
			} else {
				if(professionalByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public Professional get(Integer id) throws ProfessionalNotFoundException {
		try {
			return professionalRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProfessionalNotFoundException("Could not find any user with ID: "+ id);
		}
	}
	
	public void delete(Integer id) throws ProfessionalNotFoundException {
		Long countById = professionalRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new ProfessionalNotFoundException("Could not find any user with ID: "+ id);
		}
		
		professionalRepo.deleteById(id);
	}
	
	public Professional updateAccount(Professional professionalInForm) {     // updates an account of a logged user
		Professional professionalInDB = professionalRepo.findById(professionalInForm.getId()).get();
		
		if(!professionalInForm.getPassword().isEmpty()) {
			professionalInDB.setPassword(professionalInForm.getPassword());
			encodePassword(professionalInDB);
		}
		
		if(professionalInForm.getPhotos() !=null) {
			professionalInDB.setPhotos(professionalInForm.getPhotos());
		}

		professionalInDB.setFirstName(professionalInForm.getFirstName());
		professionalInDB.setLastName(professionalInForm.getLastName());
		return professionalRepo.save(professionalInDB);
	}
	
	public void updateProfessionalEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of the user
		professionalRepo.updateEnableStatus(id, enabled);
	}
}
