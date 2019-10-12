import React from "react";
import {Button, Container, Form, Input, Label} from "reactstrap";
import {login} from "../util/APIUtils";
import {ACCESS_TOKEN} from "../constants";

class Login extends React.Component{

    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: ''
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
        const body = {
            username: this.state.username,
            password: this.state.password
        };
        login(body)
            .then(response => {
                localStorage.setItem(ACCESS_TOKEN, response.accessToken);
                this.props.onLogin();
            }).catch(error => {
            if(error.status === 401) {
                // notification.error({
                //     message: 'Polling App',
                //     description: 'Your Username or Password is incorrect. Please try again!'
                // });
            } else {
                // notification.error({
                //     message: 'Polling App',
                //     description: error.message || 'Sorry! Something went wrong. Please try again!'
                // });
            }
        });
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
                    <Button onClick={this.handleSubmit}>Submit</Button>
                </Form>
            </Container>
        );
    }
}

export default Login;
