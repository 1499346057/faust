package org.mpi.faust.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
@Entity
@NoArgsConstructor
@Data
@Table(name = "authority")
public class Authority implements java.lang.Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    private AuthorityType name;
    public Authority(AuthorityType name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        if (o.getClass() != Authority.class) {
            return -1;
        }
        Authority rhs = (Authority) o;
        if (this.id.equals(rhs.id) && this.name.equals(rhs.name)) {
            return 0;
        }
        return this.id.compareTo(rhs.id);
    }
}