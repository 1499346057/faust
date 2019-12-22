package org.mpi.faust.dto;

import java.math.BigInteger;
import java.util.List;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getGroups() { return this.groups; }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}