import React from "react";
import {Button, Container, Form, Input, Label} from "reactstrap";
import {authenticationService} from "../common/authService";

class Login extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: ''
        };

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.login = this.login.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    login() {
        const body = {
            username: this.state.username,
            password: this.state.password
        };
        authenticationService.login(body);
    }

    render() {
        return (
            <Container>
                <Form onSubmit={this.handleSubmit}>
                    <Label>
                        Login
                        <Input
                            type="text"
                            placeholder="login"
                            value={this.state.username}
                            onChange={this.handleUsernameChange}
                        />
                    </Label>
                    <Label>
                        Password
                        <Input
                            type="password"
                            placeholder="password"
                            value={this.state.password}
                            onChange={this.handlePasswordChange}
                        />
                    </Label>
                    <Button onClick={this.login}>Submit</Button>
                </Form>
            </Container>
        );
    }
}

export default Login;
