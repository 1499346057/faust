import React, { Component } from 'react';
import {Button, Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink} from 'reactstrap';
import { Link } from 'react-router-dom';
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

        this.state.csrfToken = cookies.get('XSRF-TOKEN');
        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);
    }

    async componentDidMount() {
        const response = await fetch('/api/v1/user', {credentials: 'include'});
        const body = await response.text();
        if (response.status !== 200 || body === '') {
            this.setState(({isAuthenticated: false}))
        } else {
            this.setState({isAuthenticated: true, user: JSON.parse(body)})
            localStorage.setItem("user", JSON.stringify(this.state.user))
        }
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

    credentials
    logout() {
        fetch('/api/v1/logout', {method: 'POST', credentials: 'include',
            headers: {'X-XSRF-TOKEN': this.state.csrfToken}}).then(res => res.json())
            .then(response => {
                window.location.href = response.logoutUrl + "?id_token_hint=" +
                    response.idToken + "&post_logout_redirect_uri=" + window.location.origin;
            });
    }

    render() {
        return <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
            {
                this.state.isAuthenticated ?
                    <NavbarBrand><Button color="link"
                                         onClick={this.logout}>Logout, {this.state.user ? this.state.user.name : ""}</Button></NavbarBrand>
                    :
                    <NavbarBrand><Button color="primary" onClick={this.login}>Login</Button></NavbarBrand>
            }
        </Navbar>;
    }
}

export default withCookies(AppNavbar);