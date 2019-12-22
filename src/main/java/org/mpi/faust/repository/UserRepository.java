package org.mpi.faust.repository;

import org.mpi.faust.model.Authority;
import org.mpi.faust.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> getByAuthorities(Set<Authority> authorities);
}