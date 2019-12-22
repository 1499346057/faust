package org.mpi.faust.controller;

import org.mpi.faust.model.Exchange;
import org.mpi.faust.model.User;
import org.mpi.faust.repository.UserRepository;
import org.mpi.faust.dto.UserSummary;
import org.mpi.faust.security.CurrentUser;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return userService.getCurrentUser(currentUser);
    }

    @GetMapping("/exchanges")
    @PreAuthorize("hasRole('ROLE_USER')")
    Collection<Exchange> GetAllExchanges(@CurrentUser UserPrincipal principal) {
        return userService.GetAllExchanges(principal);
    }
}