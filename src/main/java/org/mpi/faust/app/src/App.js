import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import GroupList from './GroupList';
import GroupEdit from "./GroupEdit";
import AppNavbar from "./AppNavbar";
import Issues from "./Issues";
import IssueEdit from "./IssueEdit";
import Supplies from "./Supplies";
import SupplyEdit from "./SupplyEdit";

class App extends Component {
  render() {
    return (

        <Router>
            <div>
                <AppNavbar></AppNavbar>
                {/*<Switch>*/}
                <Route path='/' exact={true} component={Home}/>
                <Route path='/issues' exact={true} component={Issues}/>
                <Route path='/issues/:id' component={IssueEdit}/>
                <Route path='/supplies' exact={true} component={Supplies}/>
                <Route path='/supplies/:id' component={SupplyEdit}/>
                {/*</Switch>*/}
            </div>
        </Router>
    )
  }
}

export default App;