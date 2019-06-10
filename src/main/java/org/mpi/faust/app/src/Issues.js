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
        this.state = {issues: [], csrfToken: cookies.get('XSRF-TOKEN'), isLoading: true};
        this.remove = this.remove.bind(this);
    }

    componentDidMount() {
        this.setState({isLoading: true});

        fetch('api/v1/treasury/issues', {headers: { 'X-XSRF-TOKEN': this.state.csrfToken}, credentials: 'include'})
            .then(response => response.json())
            .then(data => this.setState({issues: data, isLoading: false}))
            .catch(() => this.props.history.push('/'));
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

        const issueList = issues.map(issue => {
            const address = `${issue.address || ''} ${issue.city || ''} ${issue.stateOrProvince || ''}`;
            return <tr key={issue.id}>
                <td style={{whiteSpace: 'nowrap'}}>{issue.name}</td>
                <td>{address}</td>
                <td>{issue.events.map(event => {
                    return <div key={event.id}>{new Intl.DateTimeFormat('en-US', {
                        year: 'numeric',
                        month: 'long',
                        day: '2-digit'
                    }).format(new Date(event.date))}: {event.title}</div>
                })}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/issues/" + issue.id}>Edit</Button>
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
                            <th width="20%">Name</th>
                            <th width="20%">Location</th>
                            <th>Events</th>
                            <th width="10%">Actions</th>
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
}

export default withCookies(withRouter(Issues));