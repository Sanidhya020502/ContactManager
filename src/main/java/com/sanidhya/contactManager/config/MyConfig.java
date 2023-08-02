package com.sanidhya.contactManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig  {

	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// @Bean
	// public DaoAuthenticationProvider authenticationProvider() {
	// 	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	// 	daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
	// 	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	// 	return daoAuthenticationProvider;

	// }

	/// configure method...

	// @Override
	// protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	// 	auth.authenticationProvider(authefirnticationProvider());
	// }

	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider(){
		DaoAuthenticationProvider provider =new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.getUserDetailService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	 public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception{
		return configuration.getAuthenticationManager();
	}


    @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests().requestMatchers("/admin/**").hasRole("ADMIN")
		.requestMatchers("/user/**").hasRole("USER")
		.requestMatchers("/**").permitAll()
        .and().formLogin().loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
		.failureUrl("/loginfail")
		.and().csrf().disable().httpBasic();
		http.authenticationProvider(daoAuthenticationProvider());
		DefaultSecurityFilterChain build = http.build();
		return build;		

    }

	
	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// 	http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN").antMatchers("/user/**").hasRole("USER")
	// 			.antMatchers("/**").permitAll().and().formLogin()
	// 			.loginPage("/signin")
	// 			.loginProcessingUrl("/dologin")
	// 			.defaultSuccessUrl("/user/index")				
	// 			.and().csrf().disable();
	// }

	

}