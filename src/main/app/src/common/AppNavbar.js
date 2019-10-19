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
        let list = [];
        if (this.props.isAuthenticated) {
            let isTreasury = this.props.currentUser.groups.indexOf("ROLE_TREASURY") >= 0;
            let isEmperor = this.props.currentUser.groups.indexOf("ROLE_EMPEROR") >= 0;
            let isPeople = this.props.currentUser.groups.indexOf("ROLE_PEOPLE") >= 0;
            let isSupplier = this.props.currentUser.groups.indexOf("ROLE_SUPPLIER") >= 0;
            if (isTreasury || isEmperor) {
                list.push(<NavbarBrand tag={Link} to="/issues">Issues</NavbarBrand>);
            }
            if (isTreasury || isSupplier) {
                list.push(<NavbarBrand tag={Link} to="/supplies">Supplies</NavbarBrand>);
            }
        }
        return <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">Home</NavbarBrand>
            {
                this.props.isAuthenticated ?[
                    <NavbarBrand>
                        <Button color="link" onClick={this.props.onLogout}>
                            Logout, {this.props.currentUser.name}
                        </Button>
                    </NavbarBrand>,
                    list]
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
