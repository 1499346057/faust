package org.mpi.faust.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.model.Authority;
import org.mpi.faust.model.AuthorityType;
import org.mpi.faust.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorityRepositoryUnitTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    AuthorityRepository repository;

    @Test
    public void whenFoundByName_thenReturnAuthority() {
        Authority authority = new Authority(AuthorityType.ROLE_SUPPLIER);
        entityManager.persist(authority);
        entityManager.flush();

        Optional<Authority> found = repository.findByName(AuthorityType.ROLE_SUPPLIER);

        assertThat(found.isPresent());

        assertThat(found.get().getName()).isEqualTo(authority.getName());
    }
}
