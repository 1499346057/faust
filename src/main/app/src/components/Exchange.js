import React, { Component } from 'react';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import {makeExchange, getExchangeTable, redirectHandler} from "../util/APIUtils";
import {NotificationManager} from 'react-notifications';
import NumberInput from "../util/NumberInput";
import {breakStatement} from "@babel/types";

class Exchange extends Component {

    constructor(props) {
        super(props);
        this.state = {
            table: [],
            res: {}
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        getExchangeTable()
            .then(data => this.setState({table: data}))
            .catch(error => {
                redirectHandler.call(this, error);
            });
    }

    async handleSubmit(event) {
        event.preventDefault();

        makeExchange(this.state.res).then(() => {
            NotificationManager.success('Treasury App', 'Exchange succeeded!');
            this.props.history.push('/profile');
        }).catch((error) => {
            NotificationManager.error('Treasury App', error.message || 'Sorry! Something went wrong. Please try again!');
        });
    }

    async handleChange(value, amount) {
        let res = this.state.res;
        res[value] = amount;
        this.setState({res: res});
    }

    render() {
        return <div>
            <Container>
                <Form>
                    {
                        this.state.table.map(function(row) {
                            return <NumberInput onChange={this.handleChange} value={row.value} amount={row.amount}/>
                        }, this)
                    }
                </Form>
                <Button color="primary" onClick={this.handleSubmit}>Exchange</Button>
            </Container>
        </div>
    }
}

export default Exchange;
