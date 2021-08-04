package com.wbh.admin.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

@Repository
public interface RoleRepository extends CrudRepository<Role,Integer> {

	@Query("SELECT r FROM Role r WHERE r.id = :id")
	public Role getRoleById(@Param("id") int id);
}
