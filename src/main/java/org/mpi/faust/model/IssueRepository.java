package org.mpi.faust.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface IssueRepository extends JpaRepository<Issue, Long> {
}