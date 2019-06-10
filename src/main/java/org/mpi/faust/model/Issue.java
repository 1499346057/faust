package org.mpi.faust.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.List;

@Data
//@NoArgsConstructor
//@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "treasury_issues")
public class Issue {
    @Id
    @GeneratedValue
    private Long id;

    IssueState state;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Paper> papers;
}