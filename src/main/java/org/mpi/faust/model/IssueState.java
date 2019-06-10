package org.mpi.faust.model;

import java.io.Serializable;

public enum IssueState implements Serializable {
    New,
    Approved,
    Rejected;

    public String getState() {
        return this.name();
    }
}
