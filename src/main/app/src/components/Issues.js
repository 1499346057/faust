import { Link, withRouter } from 'react-router-dom';
import { instanceOf } from 'prop-types';
import { withCookies, Cookies } from 'react-cookie';
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';

import {getIssues, removeIssue} from '../util/APIUtils';


class Issues extends Component {
    constructor(props) {
        super(props);
        this.state = {issues: []};
        this.remove = this.remove.bind(this);
    }


    componentDidMount() {
        getIssues()
            .then(data => this.setState({issues: data}))
            .catch(() => this.props.history.push('/'));
    }

    async remove(id) {
        removeIssue(id).then(() => {
            let updatedIssues = [...this.state.issues].filter(i => i.id !== id);
            this.setState({issues: updatedIssues});
        });
    }

    render() {
        const {issues} = this.state;

        let isEmperor = false;
        if (this.props.currentUser.groups) {
            isEmperor = this.props.currentUser.groups.indexOf("ROLE_EMPEROR") >= 0;
        }

        const issueList = issues.map(issue => {
            return <tr key={issue.id}>
                <td>{issue.papers.map(paper => {
                    return <div>{paper.amount} ; {paper.value}</div>
                })}</td>
                <td>{issue.state}</td>
                <td>
                    <ButtonGroup>
                        {isEmperor && issue.state === "New" ?<Button size="sm" color="success" onClick={() => this.handleApprove(issue)}>Approve</Button>:""}
                        <Button size="sm" color="danger" onClick={() => this.remove(issue.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container fluid>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/issues/new">Add Issue</Button>
                    </div>
                    <h3>Issue management</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="20%">value</th>
                            <th width="20%">state</th>
                            <th width="20%">actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {issueList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }

    async handleApprove(issue) {
        issue.state = "Approved";
        await fetch(`/api/v1/treasury/issues/`, {
            method: 'PUT',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(issue),
        }).then(() => {
            let updatedIssues = [...this.state.issues].map(i => (i.id === issue.id) ? (i.status = "Approved", i) : (i));
            this.setState({issues: updatedIssues});
        });
    }
}

export default Issues;
