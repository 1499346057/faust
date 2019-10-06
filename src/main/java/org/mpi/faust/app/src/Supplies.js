import { Link, withRouter } from 'react-router-dom';
import { instanceOf } from 'prop-types';
import { withCookies, Cookies } from 'react-cookie';
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';


class Supplys extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {supplies: [], csrfToken: cookies.get('XSRF-TOKEN'), isLoading: true, user: null};
        this.remove = this.remove.bind(this);
        this.localStorageUpdated = this.localStorageUpdated.bind(this)
    }


    async componentDidMount() {
        this.setState({isLoading: true});

        fetch('api/v1/treasury/supplies', {headers: { 'X-XSRF-TOKEN': this.state.csrfToken}, credentials: 'include'})
            .then(response => response.json())
            .then(data => this.setState({supplies: data, isLoading: false}))
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
        await fetch(`/api/v1/treasury/supplies/${id}`, {
            method: 'DELETE',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'include'
        }).then(() => {
            let updatedSupplys = [...this.state.supplies].filter(i => i.id !== id);
            this.setState({supplies: updatedSupplys});
        });
    }

    render() {
        const {supplies, isLoading} = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        var isTreasurer = false;
        if (this.state.user) {
            isTreasurer = this.state.user.groups.indexOf("Treasury") >= 0;
        }

        const supplyList = supplies.map(supply => {
            return <tr key={supply.id}>
                <td>{supply.items.map(item => {
                    return <div>{item.good} ; {item.price}</div>
                })}</td>
                <td>{supply.status}</td>
                <td>
                    <ButtonGroup>
                        {isTreasurer && supply.status === "New" ?<Button size="sm" color="success" onClick={() => this.handleApprove(supply)}>Approve</Button>:""}
                        <Button size="sm" color="danger" onClick={() => this.remove(supply.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container fluid>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/supplies/new">Add Supply</Button>
                    </div>
                    <h3>Supply management management</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="20%">items</th>
                            <th width="20%">status</th>
                            <th width="20%">actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {supplyList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }

    async handleApprove(supply) {
        supply.state = "Approved";
        await fetch(`/api/v1/treasury/supplies/`+supply.id, {
            method: 'PUT',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(supply),
        }).then(() => {
            let updatedSupplys = [...this.state.supplies].map(i => (i.id == supply.id) ? (i.status = "Approved", i) : (i));
            this.setState({supplies: updatedSupplys});
        });
    }
}

export default withCookies(withRouter(Supplys));