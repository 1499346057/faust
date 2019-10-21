import React, { Component } from 'react';
import '../App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies } from 'react-cookie';

class Home extends Component {

    render() {
        if (!this.props.isAuthenticated) {
            return (
                <Container fluid>
                    Please, authorize to proceed.
                </Container>
            );
        }
        return (
            <div>
                <Container fluid>
                    Hello, {this.props.currentUser.name}, {this.props.currentUser.groups.toString()}
                </Container>
            </div>
        )
    }
}

export default Home;
