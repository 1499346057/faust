import React from 'react';

import * as lib from '../util/APIUtils';

import SupplyEdit from './SupplyEdit';
import { shallow } from "enzyme";

import * as enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
enzyme.configure({ adapter: new Adapter() });

describe("SupplyEdit test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('good field change', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<SupplyEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        await wrapper.find("#good").simulate('change', {target: {name: 'good', value: "Sobaka"}});
        await Promise.resolve();
        expect(wrapper.find("#good").get(0).props.value).toEqual("Sobaka");
    });


    test('price field change', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<SupplyEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        await wrapper.find("#price").simulate('change', {target: {name: 'price', value: 50}});
        await Promise.resolve();
        expect(wrapper.find("#price").get(0).props.value).toEqual(50);
    });


    test('good add', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const wrapper = shallow(<SupplyEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        expect(wrapper.find("table tbody tr")).toHaveLength(0);

        await wrapper.find("#good").simulate('change', {target: {name: 'good', value: "Sobaka"}});
        await wrapper.find("#price").simulate('change', {target: {name: 'price', value: 50}});
        await wrapper.find("#rowAddButton").simulate('click');

        await Promise.resolve();

        expect(wrapper.find("tbody tr")).toHaveLength(1);
        expect(wrapper.find("tbody tr td").get(0).props.children).toEqual("Sobaka");
        expect(wrapper.find("tbody tr td").get(1).props.children).toEqual(50);
    });

    test('issue commit', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        const createIssueMock = jest.spyOn(lib, 'createSupply').mockResolvedValue(Promise.resolve());

        const wrapper = shallow(<SupplyEdit isAuthenticated={true} currentUser={currentUser} match={{params: {id: 1}}}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        expect(wrapper.find("table tbody tr")).toHaveLength(0);

        await wrapper.find("#good").simulate('change', {target: {name: 'good', value: "Sobaka"}});
        await wrapper.find("#price").simulate('change', {target: {name: 'price', value: 50}});
        await wrapper.find("#rowAddButton").simulate('click');
        await wrapper.find("#submitButton").simulate('click', {
            preventDefault: () => {}
        });

        await Promise.resolve();

        expect(wrapper.find("tbody tr")).toHaveLength(1);
        expect(wrapper.find("tbody tr td").get(0).props.children).toEqual("Sobaka");
        expect(wrapper.find("tbody tr td").get(1).props.children).toEqual(50);
        expect(createIssueMock).toHaveBeenCalledWith({
            state: "New",
            items: [
                {good: "Sobaka", price: 50}
            ]
        });
    });
});