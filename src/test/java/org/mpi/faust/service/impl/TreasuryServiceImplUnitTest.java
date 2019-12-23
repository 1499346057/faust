package org.mpi.faust.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mpi.faust.exception.BadRequestException;
import org.mpi.faust.model.*;
import org.mpi.faust.repository.AuthorityRepository;
import org.mpi.faust.repository.IssueRepository;
import org.mpi.faust.repository.SupplyRepository;
import org.mpi.faust.repository.UserRepository;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.TreasuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class TreasuryServiceImplUnitTest {

    @TestConfiguration
    static class TreasuryServiceImplTestContextConfiguration {

        @Bean
        public TreasuryService treasuryService() {
            return new TreasuryServiceImpl();
        }
    }

    @Autowired
    TreasuryService treasuryService;

    @MockBean
    private IssueRepository issueRepository;
    @MockBean
    private SupplyRepository supplyRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthorityRepository authorityRepository;

    @Before
    public void setUp() {
        {
            List<Issue> issues = new ArrayList<Issue>();
            {
                Issue issue = new Issue();
                issue.setState(IssueState.Approved);
                issue.setId(5l);
                List<Paper> papers = new ArrayList<Paper>();
                papers.add(new Paper(5l, 5l, 10l));
                papers.add(new Paper(10l, 10l, 25l));
                papers.add(new Paper(15l, 15l, 50l));
                issue.setPapers(papers);
                issues.add(issue);
            }
            Mockito.when(issueRepository.findAll()).thenReturn(issues);
        }
    }


    @Test
    public void whenEmperorApprovesIssue_thenFail() {
        {
            Authority emperorAuthority = new Authority();
            emperorAuthority.setName(AuthorityType.ROLE_EMPEROR);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_EMPEROR)).thenReturn(Optional.of(emperorAuthority));

            User emperor = new User();
            emperor.setAuthorities(Collections.singleton(emperorAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(emperorAuthority))).thenReturn(Optional.of(emperor));
        }
        Issue issue = new Issue();
        issue.setState(IssueState.Approved);
        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_EMPEROR);
        Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());

        treasuryService.modifyIssue(5l, issue, userPrincipal);
    }

    @Test(expected = BadRequestException.class)
    public void whenNotEmperorApprovesIssue_thenThrowBadRequestException() {
        {
            Authority treasuryAuthority = new Authority();
            treasuryAuthority.setName(AuthorityType.ROLE_TREASURY);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_TREASURY)).thenReturn(Optional.of(treasuryAuthority));

            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Issue issue = new Issue();
        issue.setState(IssueState.Approved);
        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY);
        Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());

        treasuryService.modifyIssue(5l, issue, userPrincipal);
    }

    @Test(expected = BadRequestException.class)
    public void whenNoSupply_thenThrowBadRequestException() {

        {
            Authority treasuryAuthority = new Authority();
            treasuryAuthority.setName(AuthorityType.ROLE_SUPPLIER);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER)).thenReturn(Optional.of(treasuryAuthority));

            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
        Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());

        Supply supply = new Supply();

        treasuryService.UpdateSupply(666l, supply, userPrincipal);
    }

    @Test(expected = BadRequestException.class)
    public void whenNoPermissionForSupplies_thenThrowBadRequestException() {
        {
            Authority treasuryAuthority = new Authority();
            treasuryAuthority.setName(AuthorityType.ROLE_USER);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_USER)).thenReturn(Optional.of(treasuryAuthority));

            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        {
            Supply supply = new Supply();
            supply.setId(666l);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }
        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_USER);
        Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());

        Supply supply = new Supply();

        treasuryService.UpdateSupply(666l, supply, userPrincipal);
    }


    @Test
    public void whenIssueIsApproved_thenMoneyTransferred() {
        {
            Authority treasuryAuthority = new Authority();
            treasuryAuthority.setName(AuthorityType.ROLE_TREASURY);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_TREASURY)).thenReturn(Optional.of(treasuryAuthority));

            User treasury = new User();
            treasury.setMoney(123l);
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        {
            Authority treasuryAuthority = new Authority();
            treasuryAuthority.setName(AuthorityType.ROLE_SUPPLIER);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER)).thenReturn(Optional.of(treasuryAuthority));

            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Supply supply = new Supply();
        {
            supply.setId(666l);
            {
                Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
                Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
                supply.setOwner(user.get());
            }
            List<SupplyItem> items = new ArrayList<SupplyItem>();
            items.add(new SupplyItem(15l, "Fuu", 123l));
            supply.setItems(items);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }

        {
            Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY);
            Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
            UserPrincipal userPrincipal = UserPrincipal.create(user.get());
            supply.setStatus("Approved");
            treasuryService.UpdateSupply(666l, supply, userPrincipal);
        }

        {
            verify(supplyRepository).saveAndFlush(supply);
        }
        {
            Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY);
            Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
            assertThat(user.get().getMoney()).isEqualTo(0l);
        }
        {
            Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
            Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
            assertThat(user.get().getMoney()).isEqualTo(123l);
        }
    }
}
