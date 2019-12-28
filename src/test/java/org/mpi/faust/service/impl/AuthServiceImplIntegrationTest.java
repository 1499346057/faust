package org.mpi.faust.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.dto.JwtAuthenticationResponse;
import org.mpi.faust.dto.LoginRequest;
import org.mpi.faust.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthServiceImplIntegrationTest {
    @Autowired
    AuthService authService;

    @Test
    public void whenAuthenticationRequestedForExistingUser_thenAuthenticate() {
        JwtAuthenticationResponse response = authService.authenticateUser(new LoginRequest("treasury", "123"));
        Assert.assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }


    @Test(expected = BadCredentialsException.class)
    public void whenAuthenticationRequestedForNonExistingUser_thenThrowUserNotFoundException() {
        authService.authenticateUser(new LoginRequest("fafaf", "123"));
        Assert.assertFalse(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }

    @Test(expected = BadCredentialsException.class)
    public void whenAuthenticationRequestedForExistingUserButPasswordIsIncorrect_thenThrowAuthenticationException() {
        authService.authenticateUser(new LoginRequest("treasury", "456"));
        Assert.assertFalse(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }
}
