package com.wbh.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomerUserDetails implements UserDetails{

	
	private User user;
	
	 public CustomerUserDetails(User customer) {
		 this.user = customer;
	 }
	
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			Set<Role> roles = user.getRoles();
			List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			
			for(Role role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			}
			return authorities;
		}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.isEnabled();
	}
	
	public String getFullName() {
		return user.getFirstName() +" "+ user.getLastName();		
	}
	
	public void setFirstName(String firstName) {
		this.user.setFirstName(firstName);
	}
	
	public void setLastName(String lastName) {
		this.user.setLastName(lastName);
	}

}
