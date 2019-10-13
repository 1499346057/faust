import React, { Component } from 'react';
import {Button, Navbar, NavbarBrand} from 'reactstrap';
import { Link } from 'react-router-dom';

import withCookies from "react-cookie/cjs/withCookies";

class AppNavbar extends Component {
    state = {
        isOpen: false,
    };

    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);
    }

    toggle() {
        this.setState({
            isOpen: !this.state.isOpen
        });
    }

    render() {
        return <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
            {
                this.props.isAuthenticated ?[
                    <NavbarBrand>
                        <Button color="link" onClick={this.props.onLogout}>
                            Logout, {this.props.currentUser.name}
                        </Button>
                    </NavbarBrand>,
                    <NavbarBrand tag={Link} to="/issues">Issues</NavbarBrand>]
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

export default AppNavbar;
