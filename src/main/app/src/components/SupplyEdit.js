import React, { Component } from 'react';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import {createSupply, getSupplies} from "../util/APIUtils";
import {NotificationManager} from "react-notifications";

class SupplyEdit extends Component {
    emptyItem = {
        items: [],
        state: "New"
    };

    constructor(props) {
        super(props);
        this.state = {
            item: this.emptyItem,
            position: {},
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleAdd = this.handleAdd.bind(this);
    }

    async componentDidMount() {
        if (this.props.match.params.id !== 'new') {
            getSupplies(this.props.match.params.id).then(supply => {
                this.setState({item: supply});
            }).catch(() => {
                this.props.history.push('/');
            });
        }
    }

    handleAdd(event) {
        const position = this.state.position;
        const items = this.state.item.items;
        items.push(position);
        const item = this.state.item;
        item.items = items;
        this.setState(item);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let position = {...this.state.position};
        position[name] = value;
        this.setState({position});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item,} = this.state;

        await createSupply(item);
        this.props.history.push('/supplies');
    }

    render() {
        const position = this.state.position;
        const item = this.state.item;
        const positionList = item.items ? item.items.map(position => {
            // key={position.id}
            return <tr key={position.id}>
                <td>{position.good}</td>
                <td>{position.price}</td>
            </tr>
        }) : "";
        const title = <h2>{item.id ? 'Edit supply' : 'Add supply'}</h2>;

        return <div>
            <Container>
                {title}
                <Form>
                    <div className="row">
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Good</Label>
                            <Input type="text" name="good" id="good" value={position.good || ''}
                                   onChange={this.handleChange} autoComplete="name"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Price</Label>
                            <Input type="text" type="number" name="price" id="price" value={position.price || ''}
                                   onChange={this.handleChange} type="number" autoComplete="address-level1"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Button color="primary" id='rowAddButton' onClick={this.handleAdd}>Add</Button>{' '}
                        </FormGroup>
                    </div>
                </Form>
                <Table className="mt-4">
                    <thead>
                    <tr>
                        <th width="50%">Good</th>
                        <th width="50%">Price</th>
                    </tr>
                    </thead>
                    <tbody>
                    {positionList}
                    </tbody>
                </Table>
                <Button color="primary" id='submitButton' onClick={this.handleSubmit}>Commit</Button>{' '}
            </Container>
        </div>
    }
}

export default SupplyEdit;
