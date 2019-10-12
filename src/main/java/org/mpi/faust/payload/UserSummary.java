package org.mpi.faust.payload;

import java.util.List;

public class UserSummary {
    private Long id;
    private String username;
    private String name;
    private List<String> groups;

    public UserSummary(Long id, String username, String name, List<String> groups) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.groups = groups;
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
}