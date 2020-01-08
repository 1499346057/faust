package org.mpi.faust.service.impl;

import org.junit.Before;
import org.junit.Ignore;
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

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class TreasuryServiceImplUnitTest {
//    @TestConfiguration
//    static class TreasuryServiceImplTestContextConfiguration {
//
//        @Bean
//        public TreasuryService treasuryService() {
//            return new TreasuryServiceImpl();
//        }
//    }

//    @Autowired
//    public TreasuryServiceImplUnitTest(TreasuryService treasuryService) {
//        this.treasuryService = treasuryService;
//    }
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
        this.treasuryService = new TreasuryServiceImpl(issueRepository, supplyRepository, userRepository, authorityRepository);
        {
            {
                Authority authority = new Authority();
                authority.setName(AuthorityType.ROLE_USER);
                Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_USER)).thenReturn(Optional.of(authority));
            }
            {
                Authority authority = new Authority();
                authority.setName(AuthorityType.ROLE_TREASURY);
                Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_TREASURY)).thenReturn(Optional.of(authority));
            }
            {
                Authority authority = new Authority();
                authority.setName(AuthorityType.ROLE_SUPPLIER);
                Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER)).thenReturn(Optional.of(authority));
            }
            {
                Authority authority = new Authority();
                authority.setName(AuthorityType.ROLE_EMPEROR);
                Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_EMPEROR)).thenReturn(Optional.of(authority));
            }
            List<Issue> issues = new ArrayList<>();
            {
                Issue issue = new Issue();
                issue.setId(5L);
                List<Paper> papers = new ArrayList<>();
                papers.add(new Paper(5L, 5L, 10L));
                papers.add(new Paper(10L, 10L, 25L));
                papers.add(new Paper(15l, 15l, 50l));
                issue.setPapers(papers);
                issues.add(issue);
            }
            Mockito.when(issueRepository.findAll()).thenReturn(issues);
            Mockito.when(issueRepository.findById(5l)).thenReturn(Optional.ofNullable(issues.get(0)));
        }
    }

    @Test
    public void whenIssueRequested_IssueReturned() {
        treasuryService.getIssue(5l);
        verify(issueRepository).findById(5l);
    }

    @Test
    public void whenIssuesRequested_thenIssuesReturned() {
        treasuryService.getIssues();
        verify(issueRepository).findAll();
    }

    @Test
    public void whenIssueDeleteRequested_thenIssueDeleted() {
        treasuryService.delIssue(5L);
        verify(issueRepository).deleteById(5L);
    }

    @Test
    public void whenEmperorApprovesIssue_thenIssueBecomesApproved() {
        Optional<Issue> oissue = issueRepository.findById(5l);
        Issue issue = new Issue(oissue.get());
        issue.setState(IssueState.Approved);
        {
            Authority emperorAuthority = authorityRepository.findByName(AuthorityType.ROLE_EMPEROR).get();
            User emperor = new User();
            emperor.setAuthorities(Collections.singleton(emperorAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(emperorAuthority))).thenReturn(Optional.of(emperor));
        }
        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_EMPEROR);
        Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());

        treasuryService.modifyIssue(5l, issue, userPrincipal);
        verify(issueRepository).save(issue);
    }

    @Test(expected = BadRequestException.class)
    public void whenNotEmperorApprovesIssue_thenThrowBadRequestException() {
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER).get();
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
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_USER).get();
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
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            List<SupplyItem> items = new ArrayList<>();
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

    @Test(expected = BadRequestException.class)
    public void whenUserHasNotEnoughMoney_thenThrowBadRequestException() {
        UserPrincipal userPrincipal;
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_USER).get();
            User user = new User();
            user.setMoney(123l);
            user.setAuthorities(Collections.singleton(treasuryAuthority));
            userPrincipal = UserPrincipal.create(user);
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(user));
        }
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Map<Long, Long> requested = new HashMap<>();
        requested.put(10L, 50L);
        treasuryService.MakeExchange(requested, userPrincipal);
    }


    @Test(expected = BadRequestException.class)
    public void whenTreasuryHasNotEnoughPapers_thenThrowBadRequestException() {
        UserPrincipal userPrincipal;
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_USER).get();
            User user = new User();
            user.setMoney(123l);
            user.setAuthorities(Collections.singleton(treasuryAuthority));
            userPrincipal = UserPrincipal.create(user);
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(user));
        }
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
            User treasury = new User();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Map<Long, Long> requested = new HashMap<>();
        requested.put(10L, 10L);
        treasuryService.MakeExchange(requested, userPrincipal);
    }


    @Test
    public void whenExchangeCanBePerformed_thenMoneyTransferredToTreasury() {
        UserPrincipal userPrincipal;
        User user;
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_USER).get();
            user = new User();
            user.setId(666l);
            user.setMoney(123l);
            user.setAuthorities(Collections.singleton(treasuryAuthority));
            user.setExchanges(new HashSet<>());
            userPrincipal = UserPrincipal.create(user);
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(user));
            Mockito.when(userRepository.getOne(user.getId())).thenReturn(user);
        }
        User treasury = new User();
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        {
            Issue issue = issueRepository.findById(5L).get();
            issue.setState(IssueState.Approved);
            Mockito.when(issueRepository.findById(5L)).thenReturn(Optional.of(issue));
        }
        Map<Long, Long> requested = new HashMap<>();
        requested.put(10L, 5L);
        treasuryService.MakeExchange(requested, userPrincipal);
        user.setMoney(user.getMoney() - 50);
        Exchange exchange = new Exchange();
        exchange.setPapers(Collections.singletonList(new Paper(20L, 5L, 5L, 10L)));
        Set<Exchange> exchanges = user.getExchanges();
        exchanges.add(exchange);
        verify(userRepository).save(user);

        treasury.setMoney(treasury.getMoney() + 50L);
        verify(userRepository).save(treasury);

        Optional<Issue> oissue = issueRepository.findById(5L);
        Issue issue = new Issue(oissue.get());
        List<Paper> papers = new ArrayList<>();
        papers.add(new Paper(0l, 5l, 10l));
        papers.add(new Paper(10l, 10l, 25l));
        papers.add(new Paper(15l, 15l, 50l));
        issue.setPapers(papers);
        verify(issueRepository).save(issue);
    }

    @Test
    public void whenSupplyIsRequested_thenItsReturned() {
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            treasury.setId(555l);
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Supply supply = new Supply();
        UserPrincipal userPrincipal;
        {
            supply.setId(666l);
            {
                Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
                Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
                supply.setOwner(user.get());
                userPrincipal = UserPrincipal.create(user.get());
            }
            List<SupplyItem> items = new ArrayList<>();
            items.add(new SupplyItem(15l, "Fuu", 123l));
            supply.setItems(items);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }

        treasuryService.GetSupply(666l, userPrincipal);
        verify(supplyRepository).findById(666l);
    }


    @Test(expected = BadRequestException.class)
    public void whenNotEnoughPermissionsToRequestSupply_thenThrowBadRequestException() {
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            treasury.setId(555l);
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Supply supply = new Supply();
        UserPrincipal userPrincipal;
        {
            supply.setId(666l);
            {
                Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
                Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
                userPrincipal = UserPrincipal.create(user.get());
                user.get().setId(777l);
                supply.setOwner(user.get());
            }
            List<SupplyItem> items = new ArrayList<>();
            items.add(new SupplyItem(15l, "Fuu", 123l));
            supply.setItems(items);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }

        treasuryService.GetSupply(666l, userPrincipal);
    }

    @Test
    public void whenSupplyDeleteRequested_thenItsReturned() {
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            treasury.setId(555l);
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Supply supply = new Supply();
        UserPrincipal userPrincipal;
        {
            supply.setId(666l);
            {
                Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
                Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
                user.get().setId(555l);
                supply.setOwner(user.get());
                userPrincipal = UserPrincipal.create(user.get());
            }
            List<SupplyItem> items = new ArrayList<>();
            items.add(new SupplyItem(15l, "Fuu", 123l));
            supply.setItems(items);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }

        treasuryService.DeleteSupply(666l, userPrincipal);
        verify(supplyRepository).findById(666l);
        verify(supplyRepository).deleteById(666l);
    }

    @Test(expected = BadRequestException.class)
    public void whenNotEnoughPermissionsToDeleteSupply_thenThrowBadRequestException() {
        {
            Authority treasuryAuthority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY).get();
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
            treasury.setId(555l);
            treasury.setAuthorities(Collections.singleton(treasuryAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(treasuryAuthority))).thenReturn(Optional.of(treasury));
        }
        Supply supply = new Supply();
        UserPrincipal userPrincipal;
        {
            supply.setId(666l);
            {
                Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER);
                Optional<User> user = userRepository.getByAuthorities(Collections.singleton(authority.get()));
                userPrincipal = UserPrincipal.create(user.get());
            }
            {
                User unrelatedUser = new User();
                unrelatedUser.setId(999L);
                supply.setOwner(unrelatedUser);
            }
            List<SupplyItem> items = new ArrayList<>();
            items.add(new SupplyItem(15l, "Fuu", 123l));
            supply.setItems(items);
            Mockito.when(supplyRepository.findById(666l)).thenReturn(Optional.of(supply));
        }

        treasuryService.DeleteSupply(666l, userPrincipal);
    }


    @Test
    public void whenAllSuppliesRequested_thenSuppliesReturned() {
        UserPrincipal userPrincipal;
        {
            Authority supplierAuthority = new Authority();
            supplierAuthority.setName(AuthorityType.ROLE_SUPPLIER);
            Mockito.when(authorityRepository.findByName(AuthorityType.ROLE_SUPPLIER)).thenReturn(Optional.of(supplierAuthority));

            User supplier = new User();
            supplier.setId(555l);
            userPrincipal = UserPrincipal.create(supplier);
            supplier.setAuthorities(Collections.singleton(supplierAuthority));
            Mockito.when(userRepository.getByAuthorities(Collections.singleton(supplierAuthority))).thenReturn(Optional.of(supplier));
        }

        treasuryService.GetAllSupplies(userPrincipal);
        verify(supplyRepository).findByOwnerId(userPrincipal.getId());
    }
}