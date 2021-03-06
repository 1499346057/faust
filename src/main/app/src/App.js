import React, { Component } from 'react';
import './App.css';
import {
    Route,
    withRouter,
    Switch
} from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import AppNavbar from "./common/AppNavbar";
import Issues from "./components/Issues";
import IssueEdit from "./components/IssueEdit";
import Supplies from "./components/Supplies";
import SupplyEdit from "./components/SupplyEdit";
import Profile from "./components/Profile"

import {NotificationContainer, NotificationManager} from 'react-notifications';

import 'react-notifications/lib/notifications.css';


import { getCurrentUser } from './util/APIUtils';

import {ACCESS_TOKEN} from "./constants";

import LoadingIndicator from "./common/LoadingIndicator";
import PrivateRoute from "./common/PrivateRoute";
import {Container} from "reactstrap";
import Footer from "./common/Footer";
import Exchange from "./components/Exchange";
import Notfound from "./components/Notfound";


class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: true
        };
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }

    loadCurrentUser() {
        this.setState({
            isLoading: true
        });
        getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response,
                    isAuthenticated: true,
                    isLoading: false
                });
            }).catch(error => {
                this.setState({
                    isLoading: false,
                });
            });
    }

    componentDidMount() {
        this.loadCurrentUser();
    }

    // Handle Logout, Set currentUser and isAuthenticated state which will be passed to other components
    handleLogout(redirectTo="/") {
        localStorage.removeItem(ACCESS_TOKEN);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });
    }

    /*
     This method is called by the Login component after successful handleSubmit
     so that we can load the logged-in user details and set the currentUser &
     isAuthenticated state, which other components will use to render their JSX
    */
    handleLogin() {
        this.loadCurrentUser();
        this.props.history.push("/");
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
    return (
            <div>
                <div className="app-container">
                    <AppNavbar isAuthenticated={this.state.isAuthenticated}
                               currentUser={this.state.currentUser}
                               onLogout={this.handleLogout} />
                    <div className="app-content">
                        <Switch>
                            <Route exact path="/" render={(props) => <Home isAuthenticated={this.state.isAuthenticated} currentUser={this.state.currentUser} handleLogout={this.handleLogout} {...props} />} />
                            <Route path='/login' exact={true} render={(props) => <Login onLogin={this.handleLogin} {...props} />} />
                            <PrivateRoute component={Issues}  isAuthenticated={this.state.isAuthenticated}  path="/issues" exact={true} currentUser={this.state.currentUser}/>
                            <PrivateRoute component={IssueEdit} isAuthenticated={this.state.isAuthenticated}  path="/issues/:id" currentUser={this.state.currentUser} />
                            <PrivateRoute component={Supplies} isAuthenticated={this.state.isAuthenticated}  path="/supplies" exact={true} currentUser={this.state.currentUser}/>
                            <PrivateRoute component={SupplyEdit} isAuthenticated={this.state.isAuthenticated}  path="/supplies/:id" currentUser={this.state.currentUser}/>
                            <PrivateRoute component={Profile} isAuthenticated={this.state.isAuthenticated}  path="/profile" exact={true} currentUser={this.state.currentUser}/>
                            <PrivateRoute component={Exchange} isAuthenticated={this.state.isAuthenticated}  path="/exchange" exact={true} currentUser={this.state.currentUser}/>
                            <Route component={Notfound}/>
                        </Switch>
                    </div>
                    <Footer />
                </div>

                <NotificationContainer/>
            </div>
    )
  }
}

/*

{/!*<Router>*!/}
{/!*    <div>*!/}
{/!*        <AppNavbar></AppNavbar>*!/}
{/!*        <Route path='/' exact={true} component={Home}/>*!/}
{/!*        <Route path='/handleSubmit' exact={true} component={Login}/>*!/}
{/!*        <Route path='/issues' exact={true} component={Issues}/>*!/}
{/!*        <Route path='/issues/:id' component={IssueEdit}/>*!/}
{/!*        <Route path='/supplies' exact={true} component={Supplies}/>*!/}
{/!*        <Route path='/supplies/:id' component={SupplyEdit}/>*!/}
{/!*    </div>*!/}
{/!*</Router>*!/}
*/

export default withRouter(App);
