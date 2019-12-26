package org.mpi.faust.repository;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.model.Authority;
import org.mpi.faust.model.AuthorityType;
import org.mpi.faust.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryUnitTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository repository;

    @Test
    public void whenFoundByUsername_returnUsername() {
        User user = new User();
        user.setUsername("test_user");
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = repository.findByUsername("test_user");

        assertThat(found.isPresent()).isTrue();

        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }


    @Test
    public void whenFoundByAuthority_returnUsername() {
        User user = new User();
        user.setUsername("test_user");
        Authority authority = new Authority();
        authority.setName(AuthorityType.ROLE_EMPEROR);
        authority = entityManager.persist(authority);
        entityManager.flush();

        Set<Authority> authorities = new HashSet<Authority>();
        authorities.add(authority);
        user.setAuthorities(authorities);
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> found = repository.getByAuthorities(authorities);

        assertThat(found.isPresent()).isTrue();

        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
    }


    @Test
    public void whenNotFoundByAuthority_returnUsername() {
        User user = new User();
        user.setUsername("test_user");

        {
            Set<Authority> authorities = new HashSet<Authority>();
            Authority authority = new Authority();
            authority.setName(AuthorityType.ROLE_EMPEROR);
            entityManager.persist(authority);
            entityManager.flush();
            authorities.add(authority);
            user.setAuthorities(authorities);
            entityManager.persist(user);
            entityManager.flush();
        }

        Set<Authority> authorities = new HashSet<Authority>();
        {
            Authority authority = new Authority();
            authority.setName(AuthorityType.ROLE_EMPEROR);
            authority = entityManager.persist(authority);
            entityManager.flush();
            authorities.add(authority);
        }

        Optional<User> found = repository.getByAuthorities(authorities);

        assertThat(found.isPresent()).isFalse();
    }
}
