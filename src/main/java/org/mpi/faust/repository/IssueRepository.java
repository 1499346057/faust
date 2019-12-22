package org.mpi.faust.repository;

import org.mpi.faust.model.Issue;
import org.mpi.faust.dto.PaperAggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Query("SELECT new org.mpi.faust.dto.PaperAggregate(SUM(papers.amount) as amount_sum, SUM(papers.total) as total_sum, papers.value as value) FROM Paper papers GROUP BY papers.value")
    Collection<PaperAggregate> getAggregatePapers();
}