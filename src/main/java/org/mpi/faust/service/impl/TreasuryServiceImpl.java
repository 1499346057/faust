package org.mpi.faust.service.impl;


import org.mpi.faust.dto.PaperAggregate;
import org.mpi.faust.exception.AppException;
import org.mpi.faust.exception.BadRequestException;
import org.mpi.faust.model.*;
import org.mpi.faust.repository.AuthorityRepository;
import org.mpi.faust.repository.IssueRepository;
import org.mpi.faust.repository.SupplyRepository;
import org.mpi.faust.repository.UserRepository;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.TreasuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TreasuryServiceImpl implements TreasuryService {
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private SupplyRepository supplyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    public TreasuryServiceImpl() {
    }

    private static boolean checkUSerForRole(UserPrincipal principal, String role) {
        return principal.getAuthorities().stream().anyMatch((p) -> ((GrantedAuthority) p).getAuthority().equals(role));
    }

    public Collection<Issue> getIssues() {
        return issueRepository.findAll();
    }

    public Optional<Issue> getIssue(Long id) {
        Optional<Issue> issue = issueRepository.findById(id);
        return issue;
    }

    public void delIssue(Long id) {
        issueRepository.deleteById(id);
    }

    public Issue createIssue(Issue issue) {
        Issue result = issueRepository.save(issue);
        return result;
    }

    public Issue modifyIssue(Long id, Issue issue, UserPrincipal principal) {
        if (issue.getState() != IssueState.New) {
            if (!checkUSerForRole(principal, "ROLE_EMPEROR")) {
                throw new BadRequestException("Not enough permissions to approve issue");
            }
        }
        Issue result = issueRepository.save(issue);
        return result;
    }


    public Collection<Supply> GetAllSupplies() {
        return supplyRepository.findAll();
    }

    public Optional<Supply> GetSupply(Long id) {
        Optional<Supply> issue = supplyRepository.findById(id);
        return issue;
    }

    public void DeleteSupply(Long id) {
        supplyRepository.deleteById(id);
    }

    public Supply CreateSupply(Supply supply, UserPrincipal principal) {
        supply.setOwner(userRepository.findById(principal.getId()).get());
        supply.setStatus("New");
        Supply result = supplyRepository.save(supply);
        return result;
    }

    public void UpdateSupply(Long id, Supply supply, UserPrincipal principal) {
        Optional<Supply> supply1_opt = supplyRepository.findById(id);
        if (!supply1_opt.isPresent()) {
            throw new BadRequestException("Supply doesn't exist");
        }
        Supply supply1 = supply1_opt.get();
        if (checkUSerForRole(principal, "ROLE_TREASURY")) {
            supply1.setStatus("Approved");
            supplyRepository.saveAndFlush(supply1);
            Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY);
            if (!authority.isPresent()) {
                throw new AppException("No Treasury role in the system");
            }
            Optional<User> treasury = userRepository.getByAuthorities(Collections.singleton(authority.get()));
            if (!treasury.isPresent()) {
                throw new AppException("No Treasury user in the system");
            }
            long money = 0;
            for (SupplyItem item : supply1.getItems()) {
                money += item.getPrice();
            }

            if (treasury.get().getMoney() < money) {
                throw new BadRequestException("Not enough money in Treasury");
            }

            // if enough money
            treasury.get().setMoney(treasury.get().getMoney() - money);
            userRepository.save(treasury.get());
            User supplier = supply.getOwner();
            supplier.setMoney(supplier.getMoney() + money);
            userRepository.save(supplier);

            return;
        } else if (checkUSerForRole(principal, "ROLE_SUPPLIER")) {
            supply1.setItems(supply.getItems());
            supply1.setStatus(supply.getStatus());
            supplyRepository.saveAndFlush(supply1);

            return;
        }

        throw new BadRequestException("Wrong permissions");
    }

    public Collection<PaperAggregate> GetExchangeTable() {
        return issueRepository.getAggregatePapers();
    }

    public void MakeExchange(Map<Long, Long> requested, UserPrincipal principal) {
        exchangeMoney(requested, principal);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    private void exchangeMoney(Map<Long, Long> requested, UserPrincipal principal) {
        long money = 0;
        for (Long val : requested.keySet()) {
            Long amount = requested.get(val);
            money += val * amount;
        }
        if (principal.getMoney() < money) {
            throw new BadRequestException("User has not enough money.");
        }
        if (requested.size() == 0) {
            return;
        }

        List<Paper> papers_final = new ArrayList<>();
        List<Issue> issues = issueRepository.findAll();
        for (Long value : requested.keySet()) {
            Paper paper_final = new Paper();
            paper_final.setAmount(0L);
            paper_final.setValue(value);
            for (Issue issue : issues) {
                boolean changed = false;
                for (Paper paper : issue.getPapers()) {
                    if (!paper.getValue().equals(value)) {
                        continue;
                    }
                    if (requested.get(value) <= paper.getAmount()) {
                        paper.setAmount(paper.getAmount() - requested.get(value));
                        paper_final.setAmount(paper_final.getAmount() + requested.get(value));
                        requested.replace(value, 0L);
                        changed = true;
                        continue;
                    }
                    requested.merge(value, -paper.getAmount(), Long::sum);
                    paper_final.setAmount(paper_final.getAmount() + paper.getAmount());
                    paper.setAmount(0L);
                    changed = true;
                }
                if (changed) {
                    issueRepository.save(issue);
                    papers_final.add(paper_final);
                }
            }
            if (requested.get(value) != 0) {
                throw new BadRequestException("Cannot fulfil request, not enough papers in Treasury.");
            }
        }

        // transactionally substract money and move papers from issues to exchange
        Exchange exchange = new Exchange();
        exchange.setPapers(papers_final);
        User currentUser = userRepository.getOne(principal.getId());
        currentUser.setMoney(currentUser.getMoney() - money);
        Set<Exchange> exchanges = currentUser.getExchanges();
        exchanges.add(exchange);
        userRepository.save(currentUser);

        Optional<Authority> authority = authorityRepository.findByName(AuthorityType.ROLE_TREASURY);
        if (!authority.isPresent()) {
            throw new AppException("No Treasury role in the system");
        }
        Optional<User> treasury = userRepository.getByAuthorities(Collections.singleton(authority.get()));
        if (!treasury.isPresent()) {
            throw new AppException("No Treasury user in the system");
        }
        treasury.get().setMoney(treasury.get().getMoney() + money);
        userRepository.save(treasury.get());
    }
}


