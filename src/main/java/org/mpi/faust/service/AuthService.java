package org.mpi.faust.service;

import org.mpi.faust.dto.JwtAuthenticationResponse;
import org.mpi.faust.dto.LoginRequest;

public interface AuthService {
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
}
