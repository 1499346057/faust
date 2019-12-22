import React, { Component } from 'react';
import '../App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';
import { withCookies } from 'react-cookie';

class Notfound extends Component {

    render() {
            return (
                <Container fluid>
                    Not found
                </Container>
            );
        }
}

export default Notfound;
