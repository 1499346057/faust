package org.mpi.faust.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.model.Authority;
import org.mpi.faust.model.AuthorityType;
import org.mpi.faust.model.Supply;
import org.mpi.faust.model.User;
import org.mpi.faust.repository.AuthorityRepository;
import org.mpi.faust.repository.IssueRepository;
import org.mpi.faust.repository.SupplyRepository;
import org.mpi.faust.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.text.html.Option;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class DatabaseIntegrityTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SupplyRepository supplyRepository;

    @Autowired
    IssueRepository issueRepository;

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    public void whenUserHaveSupplies_thenFailDeleteUser() {
        User user = new User();
        entityManager.persistFlushFind(user);
        Supply supply = new Supply();
        supply.setOwner(user);
        entityManager.persistAndFlush(supply);

        userRepository.deleteById(user.getId());
        userRepository.flush();
    }
}
