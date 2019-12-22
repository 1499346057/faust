package org.mpi.faust.model;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class SupplyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column
    private String good;

    @Column
    private Long price;
}