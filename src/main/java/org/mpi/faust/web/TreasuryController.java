package org.mpi.faust.web;


import lombok.NonNull;
import org.mpi.faust.exception.AppException;
import org.mpi.faust.exception.BadRequestException;
import org.mpi.faust.model.*;
import org.mpi.faust.security.CurrentUser;
import org.mpi.faust.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import org.javatuples.*;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/treasury")
public class TreasuryController {
    private IssueRepository issueRepository;
    private SupplyRepository supplyRepository;
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    public TreasuryController(IssueRepository issueRepository, SupplyRepository supplyRepository, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.issueRepository = issueRepository;
        this.supplyRepository = supplyRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    private static boolean checkUSerForRole(UserPrincipal principal, String role) {
        return principal.getAuthorities().stream().anyMatch((p) -> ((GrantedAuthority) p).getAuthority().equals(role));
    }

    @GetMapping("/issues")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    Collection<Issue> issues(@CurrentUser UserPrincipal principal) { return issueRepository.findAll(); }

    @GetMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<?> getIssue(@PathVariable Long id, @CurrentUser UserPrincipal principal) {
        Optional<Issue> issue = issueRepository.findById(id);
        return issue.map(response -> ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<?> delIssue(@PathVariable Long id) {
        issueRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issues")
    @PreAuthorize("hasRole('ROLE_TREASURY')")
    ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        Issue result = issueRepository.save(issue);
        return ResponseEntity.created(new URI("/api/v1/treasury/issues/" + result.getId()))
                .body(result);
    }

    @PutMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<Issue> modifyIssue(@PathVariable Long id, @Valid @RequestBody Issue issue, @CurrentUser UserPrincipal principal) {
        if (issue.getState() != IssueState.New) {
            if (!checkUSerForRole(principal, "ROLE_EMPEROR"))
            {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        Issue result = issueRepository.save(issue);
        return ok(result);
    }


    @GetMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    Collection<Supply> GetAllSupplies(){
        return supplyRepository.findAll();
    }

    @GetMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity<?> GetSupply(@PathVariable Long id){
        Optional<Supply> issue = supplyRepository.findById(id);
        return issue.map(response -> ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity DeleteSupply(@PathVariable Long id){
        supplyRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    ResponseEntity<Supply> CreateSupply(@Valid @RequestBody Supply supply, @CurrentUser UserPrincipal principal) throws URISyntaxException {
        supply.setOwner(userRepository.findById(principal.getId()).get());
        supply.setStatus("New");
        Supply result = supplyRepository.save(supply);
        return ResponseEntity.created(new URI("/api/v1/treasury/supplies/" + result.getId()))
                .body(result);
    }

    @PutMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity<?> UpdateSupply(@PathVariable Long id, @Valid @RequestBody Supply supply, @CurrentUser UserPrincipal principal) {
        Optional<Supply> supply1_opt = supplyRepository.findById(id);
        if (!supply1_opt.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Supply supply1 = supply1_opt.get();
        if(checkUSerForRole(principal, "ROLE_TREASURY")) {
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
            return ok(supply1);
        }
        else if(checkUSerForRole(principal, "ROLE_SUPPLIER")) {
            supply1.setItems(supply.getItems());
            supply1.setStatus(supply.getStatus());
            supplyRepository.saveAndFlush(supply1);
            return ok(supply1);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/exchanges")
    @PreAuthorize("hasRole('ROLE_USER')")
    Collection<PaperAggregate> GetExchangeTable(@CurrentUser UserPrincipal principal) {
        return issueRepository.getAggregatePapers();
    }

    @PostMapping(value = "/exchanges", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<?> MakeExchange(@Valid @RequestBody Map<Long, Long> requested, @CurrentUser UserPrincipal principal) {
        exchangeMoney(requested, principal);

        return ResponseEntity.ok().body(new StringResponse("Successfully exchanged money."));
    }


    @Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
    void exchangeMoney(Map<Long, Long> requested, @CurrentUser UserPrincipal principal) {
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
