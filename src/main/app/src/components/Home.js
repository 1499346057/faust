import React, { Component } from 'react';
import '../App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies } from 'react-cookie';

class Home extends Component {
    state = {
        user: null
    };

    componentDidMount() {
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

    render() {
        const message = this.state.user ?
            <h2>Welcome, {this.state.user.name}!</h2> :
            <p>Please log in to manage your JUG Tour.</p>;

        var button1 = null;
        var button2 = null;
        if (this.state.user) {
            var isEmperor = this.state.user.groups.indexOf("Emperor") >= 0;
            var isTreasury = this.state.user.groups.indexOf("Treasury") >= 0;
            var isSupplier = this.state.user.groups.indexOf("Supplier") >= 0;
            if (isEmperor || isTreasury) {
                button1 = <Button color="link"><Link to="/issues">Manage issues</Link></Button>;
            }
            if (isSupplier || isTreasury) {
                button2 = <Button color="link"><Link to="/supplies">Manage supplies</Link></Button>;
            }
        }
        else {
            button1 = <Button color="primary" tag={Link} to="/login">Login</Button>;
        }

        return (
            <div>
                <Container fluid>
                    {message}
                    {button1}
                    {button2}
                </Container>
            </div>
        );
    }
}

export default withCookies(Home);
