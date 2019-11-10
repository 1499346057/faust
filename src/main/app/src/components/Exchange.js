import React, { Component } from 'react';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import {makeExchange} from "../util/APIUtils";
import {NotificationManager} from 'react-notifications';

class Exchange extends Component {

    constructor(props) {
        super(props);
        this.state = {
        };
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async handleSubmit(event) {
        event.preventDefault();

        makeExchange(this.state.amount).then(() => {
            NotificationManager.success('Treasury App', 'Exchange succeeded!');
            this.props.history.push('/profile');
        }).catch((error) => {
            NotificationManager.error('Treasury App', error.message || 'Sorry! Something went wrong. Please try again!');
        });
    }

    render() {
        return <div>
            <Container>
                <Form>
                    <div className="row">
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Amount</Label>
                            <Input type="number" name="amount" id="amount" value={this.state.amount || ''}
                                   onChange={e => this.setState({amount: e.target.value})}/>
                        </FormGroup>
                    </div>
                </Form>
                <Button color="primary" onClick={this.handleSubmit}>Exchange</Button>
            </Container>
        </div>
    }
}

export default Exchange;
