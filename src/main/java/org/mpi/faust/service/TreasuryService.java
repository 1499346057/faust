package org.mpi.faust.service;

import org.mpi.faust.dto.PaperAggregate;
import org.mpi.faust.model.*;
import org.mpi.faust.security.UserPrincipal;
import java.util.*;

public interface TreasuryService {
    Collection<Issue> getIssues();

    Optional<Issue> getIssue(Long id);

    void delIssue(Long id);

    Issue createIssue(Issue issue);

    Issue modifyIssue(Long id, Issue issue, UserPrincipal principal);

    Collection<Supply> GetAllSupplies();

    Optional<Supply> GetSupply(Long id);

    void DeleteSupply(Long id);

    Supply CreateSupply(Supply supply, UserPrincipal principal);

    void UpdateSupply(Long id, Supply supply, UserPrincipal principal);

    Collection<PaperAggregate> GetExchangeTable();

    void MakeExchange(Map<Long, Long> requested, UserPrincipal principal);
}
