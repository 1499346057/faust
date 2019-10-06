package org.mpi.faust;

import org.mpi.faust.exception.AppException;
import org.mpi.faust.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;



@Component
class Initializer implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    public Initializer(UserRepository user_repository, AuthorityRepository authorityRepository) {
        this.userRepository = user_repository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... strings) {
        Map<AuthorityType, Authority> authorities = new HashMap<AuthorityType, Authority>();
        Stream.of(AuthorityType.ROLE_EMPEROR, AuthorityType.ROLE_USER, AuthorityType.ROLE_SUPPLIER, AuthorityType.ROLE_TREASURY).forEach(name ->
                authorities.put(name, authorityRepository.save(new Authority(name)))
        );

        User user = new User();
        user.setName("Emperor");
        user.setUsername("Emperor");
        user.setPassword("abc");
        user.setEmail("emp@treasury.com");
        Authority userRole = authorityRepository.findByName(AuthorityType.ROLE_EMPEROR)
                .orElseThrow(() -> new AppException("User Role not set."));
        user.setAuthorities(Collections.singleton(userRole));
        userRepository.save(user);

        user.setName("Treasury");
        user.setUsername("Treasury");
        user.setEmail("tres@treasury.com");
        user.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_TREASURY)));
        userRepository.save(user);

        user.setName("Supplier");
        user.setUsername("Supplier");
        user.setEmail("supploer@treasury.com");
        user.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_SUPPLIER)));
        userRepository.save(user);

        user.setName("User");
        user.setUsername("User");
        user.setEmail("user@treasury.com");
        user.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_USER)));
        userRepository.save(user);
    }
}