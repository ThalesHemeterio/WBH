package com.wbh.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	
		@Bean 
		public UserDetailsService userDetailsService() {
			return new CustomerUserDetailsService();
		}
	
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		@Bean 
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
				.antMatchers("/customers/**").authenticated()
				.antMatchers("/register/**").permitAll()
				.antMatchers("/").permitAll()
				.antMatchers("/error").permitAll()
				.and()
				.formLogin()
					.loginPage("/login")
					.defaultSuccessUrl("/customers/login", true)
					.usernameParameter("email")
					.permitAll()
					.failureHandler(authenticationFailureHandler())
				.and()
				.logout().permitAll()
				.and()
				.rememberMe()
					.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
					.tokenValiditySeconds(14 * 24 * 60 * 60)
					;
		}

	    @Bean
	    public AuthenticationFailureHandler authenticationFailureHandler() {
	        return new CustomAuthenticationFailureHandler();
	    }
	    
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/img/**","/js/**", "/webjars/**", "/style.css");
		}
	
		
	}

