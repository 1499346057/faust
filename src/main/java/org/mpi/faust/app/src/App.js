import React, { Component } from 'react';
import './App.css';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import AppNavbar from "./components/AppNavbar";
import Issues from "./components/Issues";
import IssueEdit from "./components/IssueEdit";
import Supplies from "./components/Supplies";
import SupplyEdit from "./components/SupplyEdit";

class App extends Component {
  render() {
    return (

        <Router>
            <div>
                <AppNavbar></AppNavbar>
                <Route path='/' exact={true} component={Home}/>
                <Route path='/login' exact={true} component={Login}/>
                <Route path='/issues' exact={true} component={Issues}/>
                <Route path='/issues/:id' component={IssueEdit}/>
                <Route path='/supplies' exact={true} component={Supplies}/>
                <Route path='/supplies/:id' component={SupplyEdit}/>
            </div>
        </Router>
    )
  }
}

export default App;
