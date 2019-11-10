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
@Table(name = "treasury_exchanges")
public class Exchange {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Paper> papers;
}