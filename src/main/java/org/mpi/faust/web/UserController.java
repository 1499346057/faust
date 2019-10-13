package org.mpi.faust.web;

import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import org.apache.catalina.authenticator.jaspic.SimpleAuthConfigProvider;
import org.mpi.faust.model.UserRepository;
import org.mpi.faust.payload.UserSummary;
import org.mpi.faust.security.CurrentUser;
import org.mpi.faust.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        List<String> groups = new ArrayList<>();
        currentUser.getAuthorities().forEach(authority -> groups.add(((SimpleGrantedAuthority)authority).getAuthority()));
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), groups);
        return userSummary;
    }
}