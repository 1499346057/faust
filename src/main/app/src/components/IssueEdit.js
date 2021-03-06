import React, { Component } from 'react';
import {Button, Container, Form, FormGroup, Input, Label, Table} from 'reactstrap';
import {createIssue, getIssue, putIssue} from "../util/APIUtils";
import {NotificationManager} from "react-notifications";

class IssueEdit extends Component {
    emptyItem = {
        papers: [],
        state: "New"
    };

    constructor(props) {
        super(props);
        this.state = {
            item: this.emptyItem,
            paper: {},
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleAdd = this.handleAdd.bind(this);
    }

    async componentDidMount() {
        if (this.props.match.params.id !== 'new') {
            getIssue(this.props.match.params.id).then(issue => {
                this.setState({item: issue});
            }).catch(() => {
                this.props.history.push('/');
            });
        }
    }

    async handleAdd(event) {
        const paper = this.state.paper;
        const papers = this.state.item.papers;
        if (paper.value === undefined) {
            paper.value = 1;
        }

        paper.amount = paper.total;
        papers.push(paper);
        const item = this.state.item;
        item.papers = papers;
        this.setState(item);
    }

    async handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let paper = {...this.state.paper};
        paper[name] = value;
        this.setState({paper});
    }

    async handleSubmit(event) {
        event.preventDefault();
        const {item,} = this.state;

        if (item.id === undefined) {
            await createIssue(item);
        }
        else {
            await putIssue(item);
        }
        this.props.history.push('/issues');
    }

    render() {
        const paper = this.state.paper;
        const item = this.state.item;
        const paperList = item.papers ? item.papers.map(paper => {
            // key={paper.id}
            return <tr key={paper.id}>
                <td>{paper.total}</td>
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
                            <Input type="text" type="number" name="total" id="total" value={paper.total || ''}
                                   onChange={this.handleChange} autoComplete="name"/>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Label for="value">Value</Label>
                            <Input type="select" name="value" id="value" value={paper.value || 1}
                                   onChange={this.handleChange} autoComplete="address-level1">
                                <option>1</option>
                                <option>5</option>
                                <option>50</option>
                                <option>500</option>
                            </Input>
                        </FormGroup>
                        <FormGroup className="col-md-4 mb-3">
                            <Button color="primary" id="rowAddButton" onClick={this.handleAdd}>Add</Button>{' '}
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
                <Button color="primary" id="submitButton" onClick={this.handleSubmit}>Commit</Button>{' '}
            </Container>
        </div>
    }
}

export default IssueEdit;
