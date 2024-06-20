package com.OOPS.bits_bids;

import com.OOPS.bits_bids.Entity.User;
import com.OOPS.bits_bids.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class BitsBidsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitsBidsApplication.class, args);
	}

	// Command Line Runner
	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder){
		return args -> {
			User manager = new User();
			manager.setBitsId("manager@BITSBIDS");
			manager.setPassword(passwordEncoder.encode("manager"));
			manager.setRoles("ROLE_MANAGER");

			User admin = new User();
			admin.setBitsId("admin@BITSBIDS");
			admin.setPassword(passwordEncoder.encode("admin"));
			admin.setRoles("ROLE_MANAGER,ROLE_ADMIN");

			userRepository.saveAll(List.of(admin, manager));
		};
	}

}
