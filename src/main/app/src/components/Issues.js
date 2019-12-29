import { Link } from 'react-router-dom';
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';

import {getIssues, removeIssue, redirectHandler, putIssue} from '../util/APIUtils';


class Issues extends Component {
    constructor(props) {
        super(props);
        this.state = {issues: []};
        this.remove = this.remove.bind(this);
    }


    async componentDidMount() {
        getIssues()
            .then(data => {
                this.setState({issues: data})
            })
            .catch(error => {
                redirectHandler.call(this, error);
            });
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
        let isTreasury = this.props.currentUser.groups.indexOf("ROLE_TREASURY") >= 0;

        const issueList = issues.map(issue => {
            return <tr key={issue.id}>
                <td>{issue.papers.map(paper => {
                    return <div>{paper.amount} (of total {paper.total}) x par. {paper.value}</div>
                })}</td>
                <td>{issue.state}</td>
                <td>
                    <ButtonGroup>
                        {isEmperor && issue.state === "New" ?<Button size="sm" color="success" id="approveButton" onClick={() => this.handleApprove(issue)}>Approve</Button>:""}
                        {isTreasury && issue.state === "New" ?<Button size="sm" color="success" id="editButton" tag={Link} to={"/issues/" + issue.id}>Edit</Button>:""}
                        <Button size="sm" color="danger" id='deleteButton' onClick={() => this.remove(issue.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container fluid>
                    { isTreasury?
                        <div className="float-right">
                            <Button color="success" tag={Link} to="/issues/new" id="addIssue">Add Issue</Button>
                        </div>
                        :''
                    }
                    <h3>Issue management</h3>
                    <Table className="mt-4" id="issueTable">
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
        putIssue(issue).then(() => {
            let updatedIssues = [...this.state.issues].map(i => (i.id === issue.id) ? (i.status = "Approved", i) : (i));
            this.setState({issues: updatedIssues});
        });
    }
}

export default Issues;
