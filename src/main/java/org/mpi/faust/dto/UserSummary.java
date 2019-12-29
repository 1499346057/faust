package org.mpi.faust.dto;

import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Data
public class UserSummary {
    private Long id;
    private String username;
    private String name;
    private List<String> groups;

    private long money;

    public UserSummary(Long id, String username, String name, List<String> groups, long money) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.groups = groups;
        this.money = money;
    }
}