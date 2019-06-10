import {instanceOf} from 'prop-types';
import {Cookies, withCookies} from 'react-cookie';
import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Button, ButtonGroup, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import AppNavbar from './AppNavbar';

class IssueEdit extends Component {
    static propTypes = {
        cookies: instanceOf(Cookies).isRequired
    };

    emptyItem = {
        papers: [],
        state: "New"
    };

    constructor(props) {
        super(props);
        const {cookies} = props;
        this.state = {
            item: this.emptyItem,
            paper: {},
            csrfToken: cookies.get('XSRF-TOKEN')
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleAdd = this.handleAdd.bind(this);
    }

    async componentDidMount() {
        if (this.props.match.params.id !== 'new') {
            try {
                const issue = await (await fetch(`/api/v1/issue/${this.props.match.params.id}`, {credentials: 'include'})).json();
                this.setState({item: issue});
            } catch (error) {
                this.props.history.push('/');
            }
        }
    }

    handleAdd(event) {
        var paper = this.state.paper;
        var papers = this.state.item.papers;
        papers.push(paper);
        var item = this.state.item;
        item.papers = papers;
        this.setState(item);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let paper = {...this.state.paper};
        paper[name] = value;
        this.setState({paper});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item, csrfToken} = this.state;

        await fetch('/api/v1/treasury/issues', {
            method: 'POST',
            headers: {
                //'X-XSRF-TOKEN': this.state.csrfToken,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(item),
            //credentials: 'include'
        });
        this.props.history.push('/issues');
    }

    render() {
        const paper = this.state.paper;
        const item = this.state.item;
        const paperList = item.papers ? item.papers.map(paper => {
            // key={paper.id}
            return <tr key={paper.id}>
                <td>{paper.amount}</td>
                <td>{paper.value}</td>
            </tr>
        }) : "";
        const title = <h2>{item.id ? 'Edit issue' : 'Add Issue'}</h2>;

        return <div>
            <Container>
                {title}
                <Form>
                    <div className="row">
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Amount</Label>
                            <Input type="text" name="amount" id="amount" value={paper.amount || ''}
                                   onChange={this.handleChange} autoComplete="name"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Value</Label>
                            <Input type="text" name="value" id="value" value={paper.value || ''}
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
                        <th width="50%">Amount</th>
                        <th width="50%">Value</th>
                    </tr>
                    </thead>
                    <tbody>
                    {paperList}
                    </tbody>
                </Table>
                <Button color="primary" onClick={this.handleSubmit}>Commit</Button>{' '}
            </Container>
        </div>
    }
}

export default withCookies(withRouter(IssueEdit));