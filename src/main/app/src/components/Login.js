import React from "react";
import {Button, Col, Container, Form, FormGroup, Input, Label} from "reactstrap";
import {login} from "../util/APIUtils";
import {ACCESS_TOKEN} from "../constants";
import {NotificationManager} from 'react-notifications';

class Login extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            isLoading: false
        };

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit() {
        this.setState({isLoading: true});
        const body = {
            username: this.state.username,
            password: this.state.password
        };
        login(body)
            .then(response => {
                this.setState({isLoading: false});
                localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                this.props.onLogin();
            }).catch(error => {
            this.setState({isLoading: false});
            if (error.status === 401) {
                NotificationManager.error('Treasury App', 'Your username or password is incorrect. Please try again!');
            } else {
                NotificationManager.error('Treasury App err', error.message || 'Sorry! Something went wrong. Please try again!');
            }
        });
    }

    render() {
        return (
            <Container className="App">
                <h2>Sign In</h2>
                <Form className="form">
                    <Col>
                        <FormGroup>
                            <Label>Login</Label>
                            <Input
                                type="text"
                                placeholder="login"
                                value={this.state.username}
                                onChange={this.handleUsernameChange}
                            />
                        </FormGroup>
                    </Col>
                    <Col>
                        <FormGroup>
                            <Label for="examplePassword">Password</Label>
                            <Input
                                type="password"
                                name="password"
                                placeholder="********"
                                value={this.state.password}
                                onChange={this.handlePasswordChange}
                            />
                        </FormGroup>
                    </Col>
                    <Button id="loginButton" onClick={this.handleSubmit}>Submit</Button>
                </Form>
            </Container>
        );
    }
}

export default Login;
