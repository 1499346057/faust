import React from 'react';

import * as lib from '../util/APIUtils';

import Supplies from './Supplies';
import * as enzyme from "enzyme";
import {shallow} from "enzyme";
import Adapter from 'enzyme-adapter-react-16';

enzyme.configure({adapter: new Adapter()});

describe("Supplies test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('supplies are rendered for treasury', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        let supplies = [
            {id: 1, status: "New", items: [{good: "Sobaka", price: 123}]},
            {id: 2, status: "Approved", items: []}
        ];
        jest.spyOn(lib, 'getSupplies').mockResolvedValue(Promise.resolve(supplies));
        const wrapper = shallow(<Supplies isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        const issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(2);
        const rowColumns = issueRows.get(0);
        expect(rowColumns.props.children.length).toEqual(3);
        expect(rowColumns.props.children[0].props.children[0].props.children).toEqual(["Sobaka", " ; ", 123]);
        expect(rowColumns.props.children[1].props.children).toEqual("New");
        expect(issueRows.find("#approveButton")).toHaveLength(1);
        expect(issueRows.find("#deleteButton")).toHaveLength(2);
    });

    test('supplies are rendered for supplier', async () => {
        let currentUser = {
            groups: ["ROLE_SUPPLIER"]
        };

        let supplies = [
            {id: 1, status: "New", items: [{good: "Sobaka", price: 123}]},
            {id: 2, status: "Approved", items: []}
        ];
        jest.spyOn(lib, 'getSupplies').mockResolvedValue(Promise.resolve(supplies));
        const wrapper = shallow(<Supplies isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        const issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(2);
        const rowColumns = issueRows.get(0);
        expect(rowColumns.props.children.length).toEqual(3);
        expect(rowColumns.props.children[0].props.children[0].props.children).toEqual(["Sobaka", " ; ", 123]);
        expect(rowColumns.props.children[1].props.children).toEqual("New");
        expect(issueRows.find("#approveButton")).toHaveLength(0);
        expect(issueRows.find("#deleteButton")).toHaveLength(2);
    });

    test('supply approve', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        let supplies = [
            {id: 1, status: "New", items: [{good: "Sobaka", price: 123}]},
            {id: 2, status: "Approved", items: []}
        ];

        jest.spyOn(lib, 'getSupplies').mockResolvedValue(Promise.resolve(supplies));
        const putSupplyMock = jest.spyOn(lib, 'putSupply').mockResolvedValue(Promise.resolve());
        const wrapper = shallow(<Supplies isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        await wrapper.find("#approveButton").simulate('click');

        await Promise.resolve();

        expect(wrapper.find("#approveButton")).toHaveLength(0);
        const issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(2);
        const rowColumns = issueRows.get(0);
        expect(rowColumns.props.children.length).toEqual(3);
        expect(rowColumns.props.children[1].props.children).toEqual("Approved");
        let approved_supply = supplies[0];
        approved_supply.status = "Approved";
        expect(putSupplyMock).toHaveBeenCalledWith(approved_supply);
    });

    test('supply remove', async () => {
        let currentUser = {
            groups: ["ROLE_SUPPLIER"]
        };

        let supplies = [
            {id: 1, status: "New", items: [{good: "Sobaka", price: 123}]},
            {id: 2, status: "Approved", items: []}
        ];

        jest.spyOn(lib, 'getSupplies').mockResolvedValue(Promise.resolve(supplies));
        const deleteSupplyMock = jest.spyOn(lib, 'removeSupply').mockResolvedValue(Promise.resolve());
        const wrapper = shallow(<Supplies isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();

        await Promise.resolve();

        let issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(2);

        await wrapper.find("#deleteButton").first().simulate('click');

        await Promise.resolve();

        expect(wrapper.find("#approveButton")).toHaveLength(0);
        issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(1);
        expect(deleteSupplyMock).toHaveBeenCalledWith(1);
    });
});