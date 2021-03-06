package com.wbh.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wbh.admin.user.UserService;

@RestController
public class UserRestController {

	@Autowired
	private UserService service;
	//checks if email is uniquein the database before registering a new User on the system
	@PostMapping("/admin/users/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		return service.isEmailUnique(id,email) ? "OK" : "Duplicated";
	}
}
