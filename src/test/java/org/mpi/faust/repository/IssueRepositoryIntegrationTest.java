package org.mpi.faust.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mpi.faust.dto.PaperAggregate;
import org.mpi.faust.model.Issue;
import org.mpi.faust.model.IssueState;
import org.mpi.faust.model.Paper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class IssueRepositoryIntegrationTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    IssueRepository repository;

    @Test
    public void checkExchangeTable() {
        {
            Issue issue = new Issue();
            issue.setState(IssueState.Approved);
            List<Paper> papers = new ArrayList<Paper>();
            papers.add(new Paper(5l, 5l, 10l));
            papers.add(new Paper(10l, 10l, 25l));
            papers.add(new Paper(15l, 15l, 50l));
            issue.setPapers(papers);

            entityManager.persist(issue);
            entityManager.flush();
        }

        {
            Issue issue = new Issue();
            issue.setState(IssueState.Approved);
            List<Paper> papers = new ArrayList<Paper>();
            papers.add(new Paper(5l, 5l, 10l));
            issue.setPapers(papers);

            entityManager.persist(issue);
            entityManager.flush();
        }

        Collection<PaperAggregate> manual = new ArrayList<PaperAggregate>();
        manual.add(new PaperAggregate(10l, 10l, 10l));
        manual.add(new PaperAggregate(10l, 10l, 25l));
        manual.add(new PaperAggregate(15l, 15l, 50l));

        Collection<PaperAggregate> aggregates = repository.getAggregatePapers();

        assertThat(aggregates).usingElementComparator(PaperAggregate::compareTo).isEqualTo(manual);
    }
}
