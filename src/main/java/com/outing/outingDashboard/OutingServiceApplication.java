package com.outing.outingDashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@SpringBootApplication(scanBasePackages = {"com.outing.auth","com.outing.commons", "com.outing.outingDashboard"})
@EnableFeignClients(basePackages = {"com.outing.auth","com.outing.friendship", "com.outing.expense"})
public class OutingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutingServiceApplication.class, args);
	}

	@Bean
	public Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestCustomizer(){
		return (AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry)->{
			registry
					.anyRequest().authenticated();
		};
	}
}
