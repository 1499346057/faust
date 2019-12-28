package org.mpi.faust.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mpi.faust.dto.UserSummary;
import org.mpi.faust.model.Exchange;
import org.mpi.faust.model.Paper;
import org.mpi.faust.model.User;
import org.mpi.faust.repository.UserRepository;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.UserService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringRunner.class)
public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @Before
    public void setUp() {
        this.userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void whenRequestedMe_thenReturnMe() {
        UserPrincipal principal;
        {
            User user = new User();
            user.setId(555L);
            Mockito.when(userRepository.getOne(555L)).thenReturn(user);
            principal = UserPrincipal.create(user);
        }
        UserSummary userSummary = userService.getCurrentUser(principal);
        Assert.assertEquals(userSummary.getId(), principal.getId());
        verifyZeroInteractions(userRepository);
    }

    @Test
    public void whenRequestedExchangesForUser_thenReturnExchanges() {
        Exchange exchange = new Exchange(555L, Collections.singletonList(new Paper(5L, 5L, 5L, 5L)));
        UserPrincipal principal;
        {
            User user = new User();
            user.setId(555L);
            Set<Exchange> exchanges = Collections.singleton(exchange);
            user.setExchanges(exchanges);
            Mockito.when(userRepository.getOne(555L)).thenReturn(user);
            principal = UserPrincipal.create(user);
        }
        Collection<Exchange> exchangeCollection = userService.GetAllExchanges(principal);
        Assert.assertTrue(exchangeCollection.contains(exchange));
    }
}
