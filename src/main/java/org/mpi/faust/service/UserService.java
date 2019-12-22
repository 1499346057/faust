package org.mpi.faust.service;

import org.mpi.faust.model.Exchange;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.dto.UserSummary;

import java.util.Collection;

public interface UserService {
    UserSummary getCurrentUser(UserPrincipal currentUser);

    Collection<Exchange> GetAllExchanges(UserPrincipal principal);
}
