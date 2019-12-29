package org.mpi.faust.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
