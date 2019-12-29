import { Link } from 'react-router-dom';
import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';

import {getSupplies, putSupply, removeSupply, redirectHandler} from '../util/APIUtils';




class Supplies extends Component {
    constructor(props) {
        super(props);
        this.state = {supplies: []};
        this.remove = this.remove.bind(this);
    }


    async componentDidMount() {
        getSupplies()
            .then(data => {
                this.setState({supplies: data});
            })
            .catch(error => {
                redirectHandler.call(this, error);
            });
    }

    async remove(id) {
        removeSupply(id).then(() => {
            let updatedSupplies = [...this.state.supplies].filter(i => i.id !== id);
            this.setState({supplies: updatedSupplies});
        });
    }

    render() {
        const {supplies} = this.state;

        let isTreasury = false;
        if (this.props.currentUser.groups) {
            isTreasury = this.props.currentUser.groups.indexOf("ROLE_TREASURY") >= 0;
        }
        let isSupplier = this.props.currentUser.groups.indexOf("ROLE_SUPPLIER") >= 0;

        const supplyList = supplies.map(supply => {
            return <tr key={supply.id}>
                <td>{supply.items.map(item => {
                    return <div>{item.good} ; {item.price}</div>
                })}</td>
                <td>{supply.status}</td>
                <td>
                    <ButtonGroup>
                        {isTreasury && supply.status === "New" ?<Button id="approveButton" size="sm" color="success" onClick={() => this.handleApprove(supply)}>Approve</Button>:""}
                        <Button size="sm" color="danger" id='deleteButton' onClick={() => this.remove(supply.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container fluid>
                    <div className="float-right">
                        { isSupplier?
                            <Button color="success" tag={Link} to="/supplies/new">Add Supply</Button>:''
                        }
                    </div>
                    <h3>Supply management</h3>
                    <Table className="mt-4">
                        <thead>
                        <tr>
                            <th width="20%">value</th>
                            <th width="20%">state</th>
                            <th width="20%">actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {supplyList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }

    async handleApprove(issue) {
        issue.state = "Approved";
        putSupply(issue).then(() => {
            let updatedSupplies = [...this.state.supplies].map(i => (i.id === issue.id) ? (i.status = "Approved", i) : (i));
            this.setState({supplies: updatedSupplies});
        });
    }
}

export default Supplies;
