package com.wbh.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

public interface CustomerRepository extends PagingAndSortingRepository<User, Integer> {
	
	@Query("SELECT u from User u WHERE u.email =?1")
	public User findByEmail(String email);
	
	@Query("SELECT u from User u WHERE u.verificationCode =?1")
	public User findByVerificationCode(String code);
	
	@Query("UPDATE User u SET u.enabled = true WHERE u.id =?1")
	@Modifying
	public User enable(Integer id); // used to update enable status
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getCustomerByEmail(@Param("email") String email);
	
	public Long countById(Integer id);  // used to delete users
	
	@Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',u.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE User u SET u.enabled =?2 WHERE u.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);
	
	public Page<User> findUserByRolesAndFirstNameOrLastNameOrEmailAllIgnoreCase(Role role, String firstName, String lastName,String email, Pageable pageable); // returns users with role Customer
	
	public Page<User> findUserByRoles(Role role, Pageable pageable); // returns users with role Customer

}
