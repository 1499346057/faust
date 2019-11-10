package org.mpi.faust.web;


import lombok.NonNull;
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
    public TreasuryController(IssueRepository issueRepository, SupplyRepository supplyRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.supplyRepository = supplyRepository;
        this.userRepository = userRepository;
    }

    private static boolean checkUSerForRole(UserPrincipal principal, String role)
    {
        return principal.getAuthorities().stream().anyMatch((p) -> ((GrantedAuthority) p).getAuthority().equals(role));
    }

    @GetMapping("/issues")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    Collection<Issue> issues(@CurrentUser UserPrincipal principal) {
        return issueRepository.findAll();
    }

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
        supply.setOwner(principal.getId());
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
    Collection<Exchange> GetAllExchanges(@CurrentUser UserPrincipal principal) {
        long userId = principal.getId();
        User user = userRepository.getOne(userId);
        return user.getExchanges();
    }

    // Damn copy paste
    private static Pair<Long, List<Integer>> knapSack(int W, int wt[], int n)
    {
        int i, w;
        int[][] K = new int[n + 1][W + 1];

        for (i = 0; i <= n; i++)
        {
            for (w = 0; w <= W; w++)
            {
                if (i==0 || w==0)
                    K[i][w] = 0;
                else if (wt[i-1] <= w) {
                    if (wt[i - 1] + K[i - 1][w - wt[i - 1]] > K[i - 1][w]) {
                        K[i][w] = wt[i - 1] + K[i - 1][w - wt[i - 1]];
                    }
                    else {
                        K[i][w] = K[i - 1][w];
                    }
                }
                else
                    K[i][w] = K[i-1][w];
            }
        }

        List<Integer> backtrack = new ArrayList<>();
        i = n;
        long prev = K[n][W];
        while (prev != 0) {
            if (K[i][W] != K[i - 1][W]) {
                backtrack.add(wt[i - 1]);
                prev -= wt[i - 1];
            }
            i -= 1;
        }

        return new Pair<Long, List<Integer>>((long) K[n][W], backtrack);
    }


    @PostMapping(value = "/exchanges", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<?> MakeExchange(@Valid @RequestBody int money, @CurrentUser UserPrincipal principal) {
        exchangeMoney(money, principal);

        return ResponseEntity.ok().body(new StringResponse("Successfully exchanged money."));
    }

    @Transactional(rollbackFor=Exception.class, propagation=Propagation.REQUIRED)
    void exchangeMoney(@RequestBody @Valid int money, @CurrentUser UserPrincipal principal) {
        if (principal.getMoney() < money) {
            throw new BadRequestException("Not enough money");
            //return ResponseEntity.unprocessableEntity().body("Not enough money");
        }
        if (money < 0) {
            throw new BadRequestException("Money must be non-negative integer");
            //return ResponseEntity.unprocessableEntity().body("Money must non-negative integer");
        }

        // get all papers
        int n = 0;
        List<Issue> issues = issueRepository.findAll();
        Map<Long, Long> map = new TreeMap<>();
        for (Issue issue : issues) {
            if (issue.getState() != IssueState.Approved) {
                continue;
            }
            for (Paper paper : issue.getPapers()) {
                map.merge(paper.getValue(), paper.getAmount(), Long::sum);
                n += paper.getAmount();
            }
        }
        int[] wt = new int[n];
        final int[] i = {0};
        map.forEach((Long value, Long amount) -> {
            for (int j = 0; j < amount; j++) {
                wt[i[0] + j] = value.intValue();
            }
            i[0] += amount;
        });

        Pair<Long, List<Integer>> answer = knapSack(money, wt, n);
        if (answer.getValue0() != money) {
            // "Cannot fulfil request"
            throw new BadRequestException("Cannot fulfil request");
            //return ResponseEntity.badRequest().body(answer.getValue1());
        }

        Map<Integer, Integer> reduced = new TreeMap<>();
        for (Integer b : answer.getValue1()) {
            reduced.merge(b, 1, Integer::sum);
        }
        List<Paper> papers_final = new ArrayList<>();

        for (Issue issue : issues) {
            List<Paper> papers = issue.getPapers();
            for (Paper paper : papers) {
                for (Integer key : reduced.keySet()) {
                    if (paper.getValue().intValue() == key) {
                        int minVal = min(paper.getAmount().intValue(), reduced.get(key));
                        if (reduced.get(key) == minVal) {
                            reduced.replace(key, 0);
                            issue.getPapers().get(issue.getPapers().indexOf(paper)).setAmount(paper.getAmount() - minVal);
                        }
                        Paper p = new Paper();
                        p.setValue(Long.valueOf(key));
                        p.setAmount(Long.valueOf(minVal));
                        p.setTotal(Long.valueOf(minVal));
                        papers_final.add(p);
                    }
                }
            }
            issueRepository.save(issue);
        }

        // transactionally substract money and move papers from issues to exchange
        Exchange exchange = new Exchange();
        exchange.setPapers(papers_final);
        User currentUser = userRepository.getOne(principal.getId());
        currentUser.setMoney(currentUser.getMoney() - money);
        Set<Exchange> exchanges = currentUser.getExchanges();
        exchanges.add(exchange);
        userRepository.save(currentUser);
    }

    @DeleteMapping("/exchanges/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<?> RevertExchange(@PathVariable Long id, @CurrentUser UserPrincipal principal) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
