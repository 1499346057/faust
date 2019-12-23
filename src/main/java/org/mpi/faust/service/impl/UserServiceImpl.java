package org.mpi.faust.service.impl;

import org.mpi.faust.model.Exchange;
import org.mpi.faust.model.User;
import org.mpi.faust.repository.UserRepository;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.dto.UserSummary;
import org.mpi.faust.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    public UserSummary getCurrentUser(UserPrincipal currentUser) {
        List<String> groups = new ArrayList<>();
        currentUser.getAuthorities().forEach(authority -> groups.add(((SimpleGrantedAuthority)authority).getAuthority()));
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName(), groups, currentUser.getMoney());
        return userSummary;
    }

    public Collection<Exchange> GetAllExchanges(UserPrincipal principal) {
        long userId = principal.getId();
        User user = userRepository.getOne(userId);
        return user.getExchanges();
    }
}
