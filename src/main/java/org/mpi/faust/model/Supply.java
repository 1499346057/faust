package org.mpi.faust.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="supplies")
@Data
@Builder(builderMethodName = "_builder")
@AllArgsConstructor
@NoArgsConstructor
public class Supply {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;

    @ManyToOne
    private User owner;

    @Column
    private String status;

    @OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<SupplyItem> items;
}