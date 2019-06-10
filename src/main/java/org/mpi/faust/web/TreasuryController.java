package org.mpi.faust.web;


import org.mpi.faust.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/treasury")
public class TreasuryController {
    private IssueRepository issueRepository;
    public TreasuryController(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    @GetMapping("/issues")
    @PreAuthorize("hasAnyAuthority('Emperor', 'Treasury')")
    Collection<Issue> issues(Principal principal) {
        return issueRepository.findAll();
    }

    @GetMapping("/issues/{id}")
    @PreAuthorize("hasAnyAuthority('Emperor', 'Treasury')")
    ResponseEntity<?> getIssue(@PathVariable Long id, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Issue> issue = issueRepository.findById(id);
        return issue.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/issues/{id}")
    @PreAuthorize("hasAnyAuthority('Emperor', 'Treasury')")
    ResponseEntity<?> delIssue(@PathVariable Long id) {
        issueRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/issues")
    @PreAuthorize("hasAuthority('Treasury')")
    ResponseEntity<Issue> createIssue(@Valid @RequestBody Issue issue) throws URISyntaxException {
        Issue result = issueRepository.save(issue);
        return ResponseEntity.created(new URI("/api/v1/treasury/issues/" + result.getId()))
                .body(result);
    }

    @PutMapping("/issues")
    @PreAuthorize("hasAnyAuthority('Treasury', 'Emperor')")
    ResponseEntity<Issue> modifyIssue(@Valid @RequestBody Issue issue, @AuthenticationPrincipal OAuth2User principal) {
        if (issue.getState() != IssueState.New && !principal.getAuthorities().contains("Emperor")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Issue result = issueRepository.save(issue);
        return ResponseEntity.ok(result);
    }
}
