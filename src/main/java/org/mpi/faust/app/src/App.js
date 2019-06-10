import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import GroupList from './GroupList';
import GroupEdit from "./GroupEdit";
import AppNavbar from "./AppNavbar";
import Issues from "./Issues";
import IssueEdit from "./IssueEdit";

class App extends Component {
  render() {
    return (

        <Router>
            <div>
                <AppNavbar></AppNavbar>
                {/*<Switch>*/}
                <Route path='/' exact={true} component={Home}/>
                <Route path='/groups' exact={true} component={GroupList}/>
                <Route path='/groups/:id' component={GroupEdit}/>
                <Route path='/issues' exact={true} component={Issues}/>
                <Route path='/issues/:id' component={IssueEdit}/>
                {/*</Switch>*/}
            </div>
        </Router>
    )
  }
}

export default App;