package com.wbh.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wbh.common.entity.Professional;

public class ProfessionalUserDetails implements UserDetails {

		private Professional professional;
		
		public ProfessionalUserDetails(Professional professional) {
			this.professional = professional;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return null;
		}

		@Override
		public String getPassword() {
			return professional.getPassword();
		}

		@Override
		public String getUsername() {
			return professional.getEmail();
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
			return professional.isEnabled();
		}

		public String getFullName() {
			return professional.getFirstName() + " " + professional.getLastName();
		}
		
		public Professional getProfessional() {
			return this.professional;
		}
	}
