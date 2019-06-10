package org.mpi.faust.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "papers")
public class Paper {
    @Id
    @GeneratedValue
    private Long id;
    @NonNull
    private Long amount;
    @NonNull
    private Long value;
}
