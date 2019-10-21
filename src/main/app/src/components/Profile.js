import React, {Component} from "react";
import {Jumbotron, Container, Col, Row, Table} from 'reactstrap';

class Profile extends Component {
    constructor(props) {
        super(props);
        this.state = {profile: {}};
    }

    render() {
        return (
            <div>
                <Jumbotron>
                    <Container>
                        <h1 className="display-3">{this.props.currentUser.money} coins available</h1>
                        <p className="lead">Name: {this.props.currentUser.name}</p>
                    </Container>
                </Jumbotron>
                <Container >
                    <Table>
                        <Row>
                        <Col>
                            username:
                        </Col>
                            <Col>
                                {this.props.currentUser.username}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                Roles:
                            </Col>
                            <Col>
                                {this.props.currentUser.groups}
                            </Col>
                        </Row>
                    </Table>
                </Container>
            </div>
        )
    }
}


export default Profile;