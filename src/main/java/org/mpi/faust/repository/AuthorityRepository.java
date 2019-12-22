package org.mpi.faust.repository;

import org.mpi.faust.model.Authority;
import org.mpi.faust.model.AuthorityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByName(AuthorityType type);
}