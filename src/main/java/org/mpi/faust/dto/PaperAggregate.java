package org.mpi.faust.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
public class PaperAggregate implements Comparable<PaperAggregate> {
    private final long amount;
    private final long total;
    private final long value;

    public PaperAggregate(long amount, long total, long value) {
        this.amount = amount;
        this.total = total;
        this.value = value;
    }

    @Override
    public int compareTo(PaperAggregate rhs) {
        if (this.amount == rhs.amount) {
            if (this.total == rhs.total) {
                if (this.value == rhs.value) {
                    return 0;
                }
                return Long.compare(this.value, rhs.value);
            }
            return Long.compare(this.total, rhs.total);
        }
        return Long.compare(this.amount, rhs.amount);
    }
}
