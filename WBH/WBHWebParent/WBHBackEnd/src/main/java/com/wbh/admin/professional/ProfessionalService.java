package com.wbh.admin.professional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class ProfessionalService {

	public static final int USERS_PER_PAGE = 4;
	Set<Role> roles = new HashSet<>();

	@Autowired
	private ProfessionalRepository professionalRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	// gets users by email
	public User getByEmail(String email) {
		return professionalRepo.getProfessionalByEmail(email);
	}

	// list all users
	public List<User> listAll() {
		return (List<User>) professionalRepo.findAll();
	}

	// list all user
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null) {
			return professionalRepo.findAll(keyword, pageable);
		}
		return professionalRepo.findAll(pageable);
	}

	// method that saves a new user
	public User save(User professional) {
		boolean isUpdatingProfessional = (professional.getId() != null);

		if (isUpdatingProfessional) { // checking if you're are editing or creating a user if the same email.
			User existingProfessional = professionalRepo.findById(professional.getId()).get();

			if (professional.getPassword().isEmpty()) { // password is not required in changing you can keep the same
														// just not fill the field
				professional.setPassword(existingProfessional.getPassword());
			} else {
				encodePassword(professional);
			}
		} else {
			encodePassword(professional);
		}
		return professionalRepo.save(professional);
	}

	public void encodePassword(User professional) { // Function responsible for encoding the password
		String encodedPassowrd = passwordEncoder.encode(professional.getPassword());
		professional.setPassword(encodedPassowrd);

	}

	// checks if email is unique on the database
	public boolean isEmailUnique(Integer id, String email) {
		User professionalByEmail = professionalRepo.getProfessionalByEmail(email);

		if (professionalByEmail == null)
			return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) { // checking if you're are editing or creating a user if the same email.
			if (professionalByEmail != null)
				return false;
		} else {
			if (professionalByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	// retunrs user by user ID
	public User get(Integer id) throws ProfessionalNotFoundException {
		try {
			return professionalRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProfessionalNotFoundException("Could not find any user with ID: " + id);
		}
	}

	// deletes an user professional
	public void delete(Integer id) throws ProfessionalNotFoundException {
		Long countById = professionalRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new ProfessionalNotFoundException("Could not find any user with ID: " + id);
		}

		professionalRepo.deleteById(id);
	}

	// updates a professional account of a logged user
	public User updateAccount(User professionalInForm) {
		User professionalInDB = professionalRepo.findById(professionalInForm.getId()).get();

		if (!professionalInForm.getPassword().isEmpty()) {
			professionalInDB.setPassword(professionalInForm.getPassword());
			encodePassword(professionalInDB);
		}

		if (professionalInForm.getPhotos() != null) {
			professionalInDB.setPhotos(professionalInForm.getPhotos());
		}

		professionalInDB.setFirstName(professionalInForm.getFirstName());
		professionalInDB.setLastName(professionalInForm.getLastName());
		return professionalRepo.save(professionalInDB);
	}

	public void updateProfessionalEnabledStatus(Integer id, boolean enabled) { // function to update enabled/disabled
																				// status of the user
		professionalRepo.updateEnableStatus(id, enabled);
	}

	// register new professional by Unregistered user
	public void registerProfessional(User professional) {
		encodePassword(professional);
		professional.setEnabled(false);
		professional.setCreatedTime(new Date());

		String randomCode = RandomString.make(64);
		professional.setVerificationCode(randomCode);

		professionalRepo.save(professional);
	}

	// verifies new professional account
	public boolean verify(String verificationCode) {
		User professional = professionalRepo.findByVerificationCode(verificationCode);
		if (professional == null || professional.isEnabled()) {
			return false;
		} else {
			professionalRepo.verify(professional.getId());
			return true;
		}
	}

	// list all users based on their Role professional
	public Page<User> listByPageByRole(int pageNum, String sortField, String sortDir, String keyword, Role role) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null && role != null) {
			return professionalRepo.findUserByRolesAndFirstNameOrLastNameOrEmailAllIgnoreCase(role, keyword, keyword,
					keyword, pageable);
		}
		return professionalRepo.findUserByRoles(role, pageable);
	}
}
