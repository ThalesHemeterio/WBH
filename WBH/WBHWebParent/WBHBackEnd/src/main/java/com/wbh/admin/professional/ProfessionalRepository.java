package com.wbh.admin.professional;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.wbh.common.entity.Review;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

public interface ProfessionalRepository extends PagingAndSortingRepository<User, Integer> {

	@Query("SELECT u from User u WHERE u.email =?1")
	public User findProfessionalByEmail(String email);

	@Query("SELECT u from User u WHERE u.verificationCode =?1")
	public User findByVerificationCode(String code);

	@Query("UPDATE User u SET u.enabled = true WHERE u.id =?1") // used to update enable status
	@Modifying
	public User enable(Integer id);

	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getProfessionalByEmail(@Param("email") String email);

	public Long countById(Integer id); // method use to delete users

	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);

	@Query("UPDATE User u SET u.enabled =?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled); // used to update enabled status of a professional

	@Query("UPDATE User u SET u.enabled = false, u.verificationCode = null WHERE u.id =?1")
	@Modifying
	public void verify(Integer id); //set enabled status to true after user email verification confirmation

	public Page<User> findUserByRolesAndFirstNameOrLastNameOrEmailAllIgnoreCase(Role role, String firstName,
			String lastName, String email, Pageable pageable); // returns users with role Professional

	public Page<User> findUserByRoles(Role role, Pageable pageable); // returns users with role Professional
}
