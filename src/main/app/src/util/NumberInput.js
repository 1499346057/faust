import React, { Component } from 'react';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import {getExchangeTable, getIssues, makeExchange, redirectHandler} from "../util/APIUtils";
import {NotificationManager} from 'react-notifications';

class NumberInput extends Component {

    constructor(props) {
        super(props);
        this.state = {
            amount: {}
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
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

    async handleChange(event) {
        if (event.target.value > this.props.amount || event.target.value < 0) {
            NotificationManager.error('Treasury App', 'Exchange amount must be within [0;' + this.props.amount + ')');
            return;
        }
        this.setState({amount: event.target.value});
        this.props.onChange(this.props.value, event.target.value);
    }

    render() {
        return <div>
            <Container>
                    <div className="row">
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Amount of papers of par. {this.props.value}</Label>
                            <Input type="number" name="amount" id="amount" value={this.state.amount || ''}
                                   onChange={this.handleChange} min="0" max={this.props.amount}/>
                        </FormGroup>
                    </div>
            </Container>
        </div>
    }
}

export default NumberInput;
