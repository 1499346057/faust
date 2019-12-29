import React from 'react';

import * as lib from '../util/APIUtils';
import * as enzyme from "enzyme";
import {shallow} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';

import Profile from './Profile';

enzyme.configure({adapter: new Adapter()});

describe("Profile test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('profile for user', async () => {
        let currentUser = {
            groups: ["ROLE_USER"],
            name: "foo",
            money: 500
        };

        const exchanges = [
            {papers: [{amount: 5, value: 50}, {amount: 10, value: 15}]}
        ];

        const getCurrentUserMock = jest.spyOn(lib, 'getCurrentUser').mockResolvedValue(Promise.resolve(currentUser));
        const getExchanges = jest.spyOn(lib, 'getExchanges').mockResolvedValue(Promise.resolve(exchanges));

        const wrapper = shallow(<Profile isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        await Promise.resolve();

        expect(getCurrentUserMock).toHaveBeenCalled();
        expect(getExchanges).toHaveBeenCalled();
        expect(wrapper.find(".display-3").get(0).props.children).toEqual([500, " coins available"]);
        expect(wrapper.find(".lead").get(0).props.children).toContain("Bonds: ");
    });


    test('profile for emperor', async () => {
        let currentUser = {
            groups: ["ROLE_EMPEROR"],
            name: "foo"
        };

        const exchanges = [
            {papers: [{amount: 5, value: 50}, {amount: 10, value: 15}]}
        ];

        const getCurrentUserMock = jest.spyOn(lib, 'getCurrentUser').mockResolvedValue(Promise.resolve(currentUser));
        const getExchanges = jest.spyOn(lib, 'getExchanges').mockResolvedValue(Promise.resolve(exchanges));

        const wrapper = shallow(<Profile isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        await Promise.resolve();

        expect(getCurrentUserMock).toHaveBeenCalled();
        expect(getExchanges).not.toHaveBeenCalled();
        expect(wrapper.find(".display-3").get(0).props.children).not.toContain([" coins available"]);
        expect(wrapper.find(".lead")).toHaveLength(0);
    });
});