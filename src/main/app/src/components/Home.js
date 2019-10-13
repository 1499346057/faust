import React, { Component } from 'react';
import '../App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies } from 'react-cookie';

class Home extends Component {

    render() {
        if (!this.props.isAuthenticated) {
            return (
                <div>
                    <Button color="primary" tag={Link} to="/login">Login</Button>
                </div>
            );
        }
        return (
            <div>
                Hello, {this.props.currentUser.name}, {this.props.currentUser.groups.toString()}
            </div>
        )
    }
}

export default Home;
