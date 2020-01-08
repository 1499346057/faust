package org.mpi.faust;

import org.mpi.faust.exception.AppException;
import org.mpi.faust.model.*;
import org.mpi.faust.repository.AuthorityRepository;
import org.mpi.faust.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;


@Component
@Profile("volume")
class VolumeInitializer implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    public VolumeInitializer(UserRepository user_repository, AuthorityRepository authorityRepository) {
        this.userRepository = user_repository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... strings) {
        Map<AuthorityType, Authority> authorities = new HashMap<AuthorityType, Authority>();
        Stream.of(AuthorityType.ROLE_EMPEROR, AuthorityType.ROLE_USER, AuthorityType.ROLE_SUPPLIER, AuthorityType.ROLE_TREASURY).forEach(name ->
                authorities.put(name, authorityRepository.save(new Authority(name)))
        );

        List<User> users = new ArrayList<>();

        User emperor = new User();
        emperor.setName("emperor");
        emperor.setUsername("emperor");
        emperor.setPassword(passwordEncoder.encode("123"));
        emperor.setEmail("emp@treasury.com");
        Authority userRole = authorityRepository.findByName(AuthorityType.ROLE_EMPEROR)
                .orElseThrow(() -> new AppException("User Role not set."));
        emperor.setAuthorities(Collections.singleton(userRole));
        users.add(emperor);

        User treasury = new User();
        treasury.setName("treasury");
        treasury.setUsername("treasury");
        treasury.setPassword(passwordEncoder.encode("123"));
        treasury.setEmail("tres@treasury.com");
        treasury.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_TREASURY)));
        treasury.setMoney(100);
        users.add(treasury);

        User supplier = new User();
        supplier.setName("supplier");
        supplier.setUsername("supplier");
        supplier.setPassword(passwordEncoder.encode("123"));
        supplier.setEmail("supplier@treasury.com");
        supplier.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_SUPPLIER)));
        users.add(supplier);

        User user = new User();
        user.setName("user");
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123"));
        user.setEmail("user@treasury.com");
        user.setAuthorities(Collections.singleton(authorities.get(AuthorityType.ROLE_USER)));
        user.setMoney(500);
        users.add(user);

        userRepository.saveAll(users);

        for (long i = 0; i < 1000; i++) {
            Issue issue = new Issue();
            issue.setState(IssueState.New);
        }
    }
}