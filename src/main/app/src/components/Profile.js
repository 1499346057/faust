import React, {Component} from "react";
import {Jumbotron, Container, Col, Row, Table, ButtonGroup, Button} from 'reactstrap';
import {getCurrentUser, getExchanges, redirectHandler} from "../util/APIUtils";
import LoadingIndicator from "../common/LoadingIndicator";

class Profile extends Component {
    constructor(props) {
        super(props);
        this.state = {currentUser: {}, profile: {}, exchanges: [], isLoading: true};
    }

    async componentDidMount() {
        getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response
                });

                let isUser = this.state.currentUser.groups.indexOf("ROLE_USER") >= 0;
                if (isUser) {
                    getExchanges()
                        .then(ex => {
                            this.setState({exchanges: ex});
                            this.setState({isLoading: false});
                        })
                        .catch(error => {
                            redirectHandler.call(this, error);
                        });
                }
                this.setState({isLoading: false});
            }).catch(error => {
                redirectHandler.call(this, error);
            });
    }

    render() {
        if(this.state.isLoading) {
            return (
                <div>
                    <div className="app-container">
                        <LoadingIndicator />
                    </div>
                </div>
            );
        }
        let isUser = this.state.currentUser.groups.indexOf("ROLE_USER") >= 0;
        let isEmperor= this.state.currentUser.groups.indexOf("ROLE_EMPEROR") >= 0;
        let jumbotronContent = [];
        if (!isEmperor) {
            jumbotronContent.push(<h1 className="display-3">{this.state.currentUser.money} coins available</h1>);
        }
        if (isUser) {
            jumbotronContent.push((this.state.exchanges && this.state.exchanges.length)?<p className="lead">Bonds: {this.printExchanges(this.state.exchanges)} </p>:"");
            jumbotronContent.push(<p className="lead">Name: {this.state.currentUser.name}</p>);
        }
        else {
            jumbotronContent.push(<h1 className="display-3">{this.state.currentUser.name}</h1>);
        }
        return (
            <div>
                <Jumbotron>
                    <Container>
                        {jumbotronContent}
                    </Container>
                </Jumbotron>
                <Container >
                    <Table>
                        <Row>
                        <Col>
                            username:
                        </Col>
                            <Col>
                                {this.state.currentUser.username}
                            </Col>
                        </Row>
                        <Row>
                            <Col>
                                Roles:
                            </Col>
                            <Col>
                                {this.state.currentUser.groups.join(', ')}
                            </Col>
                        </Row>
                    </Table>
                </Container>
            </div>
        )
    }

    printExchanges(exchanges) {
        return exchanges.map(exchange => {
            return exchange.papers.map(paper => {
                return <span>{paper.amount} x par. {paper.value} | </span>
            })
        });
    }
}


export default Profile;