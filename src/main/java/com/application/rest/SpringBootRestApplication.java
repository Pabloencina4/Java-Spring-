package com.application.rest;

import com.application.rest.entities.ERole;
import com.application.rest.entities.RoleEntity;
import com.application.rest.entities.UserEntity;
import com.application.rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SpringBootRestApplication {



	public static void main(String[] args) {
        SpringApplication.run(SpringBootRestApplication.class, args);
	}

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Bean
    CommandLineRunner init(){

        return args -> {
            UserEntity userEntity = UserEntity.builder()
                    .email("pepe@mail.com")
                    .username("pepe")
                    .password(passwordEncoder.encode("123"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.ADMIN.name()))
                            .build()))
                    .build();

            UserEntity userEntity2 = UserEntity.builder()
                    .email("luna@mail.com")
                    .username("luna")
                    .password(passwordEncoder.encode("123"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.USER.name()))
                            .build()))
                    .build();

            userRepository.save(userEntity);
            userRepository.save(userEntity2);
        };
    }
}
