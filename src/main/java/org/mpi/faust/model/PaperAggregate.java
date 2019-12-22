package org.mpi.faust.model;

public class PaperAggregate {
    private final long amount;
    private final long total;
    private final long value;

    public PaperAggregate(long amount, long total, long value) {
        this.amount = amount;
        this.total = total;
        this.value = value;
    }

    public long getAmount() {
        return amount;
    }

    public long getTotal() {
        return total;
    }

    public long getValue() {
        return value;
    }
}
