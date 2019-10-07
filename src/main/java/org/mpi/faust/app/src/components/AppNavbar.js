import React, { Component } from 'react';
import {Button, Navbar, NavbarBrand} from 'reactstrap';
import { Link } from 'react-router-dom';
import {authenticationService} from "../common/authService";

import withCookies from "react-cookie/cjs/withCookies";

class AppNavbar extends Component {
    state = {
        isLoading: true,
        isOpen: false,
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.toggle = this.toggle.bind(this);


        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);

        this.state = {
            currentUser: authenticationService.currentUserValue,
            users: null,
            csrfToken: cookies.get('XSRF-TOKEN')
        };
    }

    async componentDidMount() {
        authenticationService.login();
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    login() {
        let port = (window.location.port ? ':' + window.location.port : '');
        if (port === ':3000') {
            port = ':8080';
        }
        window.location.href = '//' + window.location.hostname + port + '/oauth2/authorization/okta';
    }

    logout() {
        fetch('/api/v1/logout', {method: 'POST', credentials: 'include',
            headers: {'X-XSRF-TOKEN': this.state.csrfToken}}).then(res => res.json())
            .then(response => {
                window.location.href = response.logoutUrl + "?id_token_hint=" +
                    response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
            });
        authenticationService.logout();
    }

    render() {
        return <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
            {
                this.state.currentUser != null ?
                    <NavbarBrand>
                        <Button color="link" onClick={this.logout}>
                            Logout, {this.state.currentUser.name}
                        </Button>
                    </NavbarBrand>
                    :
                    <NavbarBrand>
                        <Button color="primary" tag={Link} to="/login">
                            Login
                        </Button>
                    </NavbarBrand>
            }
        </Navbar>;
    }
}

export default withCookies(AppNavbar);
