import {instanceOf} from 'prop-types';
import {Cookies, withCookies} from 'react-cookie';
import React, {Component} from 'react';
import {withRouter} from 'react-router-dom';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';

class IssueEdit extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    emptyItem = {
        items: [],
        status: "New"
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {
            item: this.emptyItem,
            supply: {},
            csrfToken: cookies.get('XSRF-TOKEN')
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleAdd = this.handleAdd.bind(this);
    }

    async componentDidMount() {
        if (this.props.match.params.id !== 'new') {
            try {
                const supply = await (await fetch(`/api/v1/treasury/supplies/${this.props.match.params.id}`, {credentials: 'include'})).json();
                this.setState({item: supply});
            } catch (error) {
                this.props.history.push('/');
            }
        }
    }

    handleAdd(event) {
        var supply = this.state.supply;
        var supplies = this.state.item.items;
        supplies.push(supply);
        var item = this.state.item;
        item.items = supplies;
        this.setState(item);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let supply = {...this.state.supply};
        supply[name] = value;
        this.setState({supply});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item,} = this.state;

        await fetch('/api/v1/treasury/supplies', {
            method: 'POST',
            headers: {
                'X-XSRF-TOKEN': this.state.csrfToken,   // ???
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
            credentials: 'include'
        });
        this.props.history.push('/supplies');
    }

    render() {
        const supply = this.state.supply;
        const item = this.state.item;
        const supplyList = item.items ? item.items.map(supplyItem => {
            // key={supply.id}
            return <tr key={supplyItem.id}>
                <td>{supplyItem.good}</td>
                <td>{supplyItem.price}</td>
            </tr>
        }) : "";
        const title = <h2>{item.id ? 'Edit supply' : 'Add supply'}</h2>;

        return <div>
            <Container>
                {title}
                <Form>
                    <div className="row">
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="good">Good</Label>
                            <Input type="text" name="good" id="good" value={supply.good || ''}
                                   onChange={this.handleChange} autoComplete="name"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="price">price</Label>
                            <Input type="text" name="price" id="price" value={supply.price || ''}
                                   onChange={this.handleChange} autoComplete="address-level1"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Button color="primary" onClick={this.handleAdd}>Add</Button>{' '}
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
                    {supplyList}
                    </tbody>
                </Table>
                <Button color="primary" onClick={this.handleSubmit}>Commit</Button>{' '}
            </Container>
        </div>
    }
}

export default withCookies(withRouter(IssueEdit));
