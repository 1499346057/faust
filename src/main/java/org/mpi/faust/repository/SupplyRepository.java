package org.mpi.faust.repository;

import org.mpi.faust.model.Supply;
import org.mpi.faust.security.UserPrincipal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface SupplyRepository extends JpaRepository<Supply, Long> {
    Collection<Supply> findByOwnerId(Long id);
}