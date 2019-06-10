import React, { Component } from 'react';
import './App.css';
import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies } from 'react-cookie';

class Home extends Component {
    state = {
        user: null
    };

    constructor(props) {
        super(props);

    }

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

        var button = null;
        if (this.state.user) {
            var isEmperor = this.state.user.groups.indexOf("Emperor") >= 0;
            var isTreasury = this.state.user.groups.indexOf("Treasury") >= 0;
            if (isEmperor || isTreasury) {
                button = <Button color="link"><Link to="/issues">Manage issues</Link></Button>;
            }
            // button = <div>
            //     <Button color="link"><Link to="/groups">Manage JUG Tour</Link></Button>
            //     {this.state.user.groups.indexOf("Emperor") >= 0? "Emperor" : "Not emperor"}
            //     <br/>
            //     <Button color="link" onClick={this.logout}>Logout</Button>
            // </div>
        }
        else {
            button = <Button color="primary" onClick={this.login}>Login</Button>;
        }

        return (
            <div>
                <Container fluid>
                    {message}
                    {button}
                </Container>
            </div>
        );
    }
}

export default withCookies(Home);