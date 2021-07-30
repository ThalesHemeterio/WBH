package com.wbh.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@Order(1)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean	
	public UserDetailsService userDetailsService() {
		return new WBHUserDetailsService();
	}
	
	@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	
		@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
			.antMatchers("/users/**").hasAuthority("Admin")
			.antMatchers("/customers/**").hasAuthority("Admin")
			.antMatchers("/categories/**").hasAuthority("Admin")
			.antMatchers("/sessions/**").hasAuthority("Admin")
			.antMatchers("/professionals/**").hasAuthority("Admin")
			.antMatchers("/reviews/**").hasAuthority("Admin")
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.usernameParameter("email")
				.permitAll()
				.and().logout().permitAll()
				.and()
					.rememberMe()
					.key("AbcDefgHijKlmOpqrs_1234567890")
					.tokenValiditySeconds(7*24*60*60); //cookies valid for 1 week
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/img/**","/js/**", "/webjars/**", "/style.css");
		}
		
		
		
	}

