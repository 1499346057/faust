import React from 'react';

import * as lib from '../util/APIUtils';
import * as enzyme from "enzyme";
import {shallow} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';
import {ACCESS_TOKEN} from "../constants";

import Login from './Login';

import {NotificationManager} from 'react-notifications';

enzyme.configure({adapter: new Adapter()});

describe("Login test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('login succeeded', async () => {
        let currentUser = {};

        const loginResponse = {
            accessToken: "awesome token"
        };

        const loginMock = jest.spyOn(lib, 'login').mockResolvedValue(Promise.resolve(loginResponse));

        const wrapper = shallow(<Login isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        // await instance.componentDidMount();
        await wrapper.find({type: 'text'}).simulate('change', {target: {value: "foo"}});
        await wrapper.find({type: 'password'}).simulate('change', {target: {value: "bar"}});
        await wrapper.find('#loginButton').simulate('click');
        await Promise.resolve();
        expect(loginMock).toHaveBeenCalled();
        expect(localStorage.getItem(ACCESS_TOKEN)).toEqual(loginResponse.accessToken);
    });


    test('login failed', async () => {
        let currentUser = {};

        let error = {
            status: 401
        };

        const loginMock = jest.spyOn(lib, 'login').mockResolvedValue(Promise.reject(error));
        const notificationManagerErrorMock = jest.spyOn(NotificationManager, 'error');

        const wrapper = shallow(<Login isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        // await instance.componentDidMount();
        await wrapper.find({type: 'text'}).simulate('change', {target: {value: "foo"}});
        await wrapper.find({type: 'password'}).simulate('change', {target: {value: "bar"}});
        await wrapper.find('#loginButton').simulate('click');
        await Promise.resolve();
        expect(loginMock).toHaveBeenCalled();
        expect(notificationManagerErrorMock).toHaveBeenCalled();
    });
});