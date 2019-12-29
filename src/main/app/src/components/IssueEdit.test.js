import React from 'react';

import * as lib from '../util/APIUtils';

import IssueEdit from './IssueEdit';
import { shallow } from "enzyme";

import * as enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
enzyme.configure({ adapter: new Adapter() });

describe("Issue edit test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('amount field change', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<IssueEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        await wrapper.find("#total").simulate('change', {target: {name: 'total', value: 555}});
        await Promise.resolve();
        expect(wrapper.find("#total").get(0).props.value).toEqual(555);
    });


    test('value field change', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<IssueEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        await wrapper.find("#value").simulate('change', {target: {name: 'value', value: 50}});
        await Promise.resolve();
        expect(wrapper.find("#value").get(0).props.value).toEqual(50);
    });


    test('paper add', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<IssueEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        expect(wrapper.find("table tbody tr")).toHaveLength(0);

        await wrapper.find("#total").simulate('change', {target: {name: 'total', value: 555}});
        await wrapper.find("#value").simulate('change', {target: {name: 'value', value: 50}});
        await wrapper.find("#rowAddButton").simulate('click');

        await Promise.resolve();

        expect(wrapper.find("tbody tr")).toHaveLength(1);
        expect(wrapper.find("tbody tr td").get(0).props.children).toEqual(555);
        expect(wrapper.find("tbody tr td").get(1).props.children).toEqual(50);
    });

    test('issue commit', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const createIssueMock = jest.spyOn(lib, 'createIssue').mockResolvedValue(Promise.resolve());

        const wrapper = shallow(<IssueEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        expect(wrapper.find("table tbody tr")).toHaveLength(0);

        await wrapper.find("#total").simulate('change', {target: {name: 'total', value: 555}});
        await wrapper.find("#value").simulate('change', {target: {name: 'value', value: 50}});
        await wrapper.find("#rowAddButton").simulate('click');
        await wrapper.find("#submitButton").simulate('click', {
            preventDefault: () => {}
        });

        await Promise.resolve();

        expect(wrapper.find("tbody tr")).toHaveLength(1);
        expect(wrapper.find("tbody tr td").get(0).props.children).toEqual(555);
        expect(wrapper.find("tbody tr td").get(1).props.children).toEqual(50);
        expect(createIssueMock).toHaveBeenCalledWith({
            state: "New",
            papers: [
                {total: 555, value: 50, amount: 555}
            ]
        });
    });
});