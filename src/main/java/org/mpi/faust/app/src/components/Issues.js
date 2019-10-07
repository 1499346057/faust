import { Link, withRouter } from 'react-router-dom';
import { instanceOf } from 'prop-types';
import { withCookies, Cookies } from 'react-cookie';
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';


class Issues extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {issues: [], csrfToken: cookies.get('XSRF-TOKEN'), isLoading: true, user: null};
        this.remove = this.remove.bind(this);
        this.localStorageUpdated = this.localStorageUpdated.bind(this)
    }


    async componentDidMount() {
        this.setState({isLoading: true});

        fetch('api/v1/treasury/issues', {headers: { 'X-XSRF-TOKEN': this.state.csrfToken}, credentials: 'include'})
            .then(response => response.json())
            .then(data => this.setState({issues: data, isLoading: false}))
            .catch(() => this.props.history.push('/'));

        if (typeof window !== 'undefined') {
            window.addEventListener('storage', this.localStorageUpdated)
            this.setState({user: JSON.parse(localStorage.getItem("user"))})
        }
    }


    localStorageUpdated(){
        if (localStorage.getItem('user')) {
            this.setState({user: JSON.parse(localStorage.getItem("user"))})
        }
        this.forceUpdate();
    }

    async remove(id) {
        await fetch(`/api/v1/treasury/issues/${id}`, {
            method: 'DELETE',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        }).then(() => {
            let updatedIssues = [...this.state.issues].filter(i => i.id !== id);
            this.setState({issues: updatedIssues});
        });
    }

    render() {
        const {issues, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        let isEmperor = false;
        if (this.state.user) {
            isEmperor = this.state.user.groups.indexOf("Emperor") >= 0;
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

export default withCookies(withRouter(Issues));
