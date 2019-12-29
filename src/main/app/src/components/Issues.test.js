import React from 'react';

import * as lib from '../util/APIUtils';

import Issues from './Issues';
import renderer from 'react-test-renderer';
import { shallow } from "enzyme";

import {MemoryRouter as Router} from 'react-router-dom';

import * as enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
enzyme.configure({ adapter: new Adapter() });

const renderWithRouter = node => renderer.create(<Router>{node}</Router>);
describe("Issues test pack", () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('treasury has link to new issue form', () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };
        const component = renderWithRouter(
            <Issues isAuthenticated={true} currentUser={currentUser}/>,
        );
        const testInstance = component.root;
        expect(testInstance.findByProps({to: "/issues/new"})).toBeTruthy();
    });


    test('everyone else hasnt link to new issue form', () => {
        let currentUser = {
            groups: ["ROLE_EMPEROR"]
        };
        const component = renderWithRouter(
            <Issues isAuthenticated={true} currentUser={currentUser}/>,
        );
        const testInstance = component.root;
        expect(function () {
            testInstance.findByProps({to: "/issues/new"})
        }).toThrow(new Error("No instances found with props: {\"to\":\"/issues/new\"}"));
    });

    test('issues are rendered', async () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        let issues = [
            {id: 1, state: "New", papers: []},
            {id: 2, state: "Approved", papers: []}
        ];
        jest.spyOn(lib, 'getIssues').mockResolvedValue(Promise.resolve(issues));
        const wrapper = shallow(<Issues isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        const issueRows = wrapper.find("tbody tr");
        expect(issueRows).toHaveLength(2);
        const rowColumns = issueRows.get(0);
        expect(rowColumns.props.children.length).toEqual(3);
        expect(rowColumns.props.children[0].props.children.length).toEqual(0);
        expect(rowColumns.props.children[1].props.children).toEqual("New");
        expect(issueRows.find("#editButton")).toHaveLength(1);
        expect(issueRows.find("#deleteButton")).toHaveLength(2);
    });


    test('emperor can approve issue', async () => {
        let currentUser = {
            groups: ["ROLE_EMPEROR"]
        };

        let issues = [
            {id: 1, state: "New", papers: []}
        ];
        jest.spyOn(lib, 'getIssues').mockResolvedValue(Promise.resolve(issues));
        const putIssueMock = jest.spyOn(lib, 'putIssue').mockResolvedValue(Promise.resolve());
        const wrapper = shallow(<Issues isAuthenticated={true} currentUser={currentUser}/>);
        const instance = wrapper.instance();
        await instance.componentDidMount();
        const issueRows = wrapper.find("tbody tr");
        expect(issueRows.find("#approveButton")).toHaveLength(1);
        await issueRows.find("#approveButton").simulate('click');
        expect(putIssueMock).toHaveBeenCalled();
        await Promise.resolve();
        expect(wrapper.find("#approveButton")).toHaveLength(0);
    });

});