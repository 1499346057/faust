package org.mpi.faust.controller;


import org.mpi.faust.dto.PaperAggregate;
import org.mpi.faust.dto.StringResponse;
import org.mpi.faust.model.*;
import org.mpi.faust.security.CurrentUser;
import org.mpi.faust.security.UserPrincipal;
import org.mpi.faust.service.TreasuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/api/v1/treasury")
public class TreasuryController {
    @Autowired
    TreasuryService treasuryService;

    public TreasuryController() {
    }

    @GetMapping("/issues")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    Collection<Issue> issues(@CurrentUser UserPrincipal principal) { return treasuryService.getIssues(); }

    @GetMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<?> getIssue(@PathVariable Long id, @CurrentUser UserPrincipal principal) {
        Optional<Issue> issue = treasuryService.getIssue(id);
        return issue.map(response -> ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<?> delIssue(@PathVariable Long id) {
        treasuryService.delIssue(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issues")
    @PreAuthorize("hasRole('ROLE_TREASURY')")
    ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        Issue result = treasuryService.createIssue(issue);
        return ResponseEntity.created(new URI("/api/v1/treasury/issues/" + result.getId()))
                .body(result);
    }

    @PutMapping("/issues/{id}")
    @PreAuthorize("hasRole('ROLE_EMPEROR') or hasRole('ROLE_TREASURY')")
    ResponseEntity<Issue> modifyIssue(@PathVariable Long id, @Valid @RequestBody Issue issue, @CurrentUser UserPrincipal principal) {
        return ok(treasuryService.modifyIssue(id, issue, principal));
    }


    @GetMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    Collection<Supply> GetAllSupplies(){
        return treasuryService.GetAllSupplies();
    }

    @GetMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity<?> GetSupply(@PathVariable Long id){
        Optional<Supply> issue = treasuryService.GetSupply(id);
        return issue.map(response -> ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity DeleteSupply(@PathVariable Long id){
        treasuryService.DeleteSupply(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    ResponseEntity<Supply> CreateSupply(@Valid @RequestBody Supply supply, @CurrentUser UserPrincipal principal) throws URISyntaxException {
        Supply result = treasuryService.CreateSupply(supply, principal);
        return ResponseEntity.created(new URI("/api/v1/treasury/supplies/" + result.getId()))
                .body(result);
    }

    @PutMapping("/supplies/{id}")
    @PreAuthorize("hasRole('ROLE_TREASURY') or hasRole('ROLE_SUPPLIER')")
    ResponseEntity<?> UpdateSupply(@PathVariable Long id, @Valid @RequestBody Supply supply, @CurrentUser UserPrincipal principal) {
        treasuryService.UpdateSupply(id, supply, principal);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/exchanges")
    @PreAuthorize("hasRole('ROLE_USER')")
    Collection<PaperAggregate> GetExchangeTable(@CurrentUser UserPrincipal principal) {
        return treasuryService.GetExchangeTable();
    }

    @PostMapping(value = "/exchanges", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    ResponseEntity<?> MakeExchange(@Valid @RequestBody Map<Long, Long> requested, @CurrentUser UserPrincipal principal) {
        treasuryService.MakeExchange(requested, principal);

        return ResponseEntity.ok().body(new StringResponse("Successfully exchanged money."));
    }
}
