package org.mpi.faust.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "treasury_issues")
public class Issue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    IssueState state = IssueState.New;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Paper> papers;

    public Issue(Issue issue) {
        this.id = issue.getId();
        this.state = issue.getState();
        this.papers = issue.getPapers();
    }
}