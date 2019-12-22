import React from 'react';

import * as lib from '../util/APIUtils';

import Issues from './Issues';
import renderer from 'react-test-renderer';

import {MemoryRouter as Router} from 'react-router-dom';

const renderWithRouter = node => renderer.create(<Router>{node}</Router>);
describe("Issues test pack", () => {
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

    test('issues are rendered', () => {
        let currentUser = {
            groups: ["ROLE_TREASURY"]
        };

        let issues = [
            {id: 1, state: "New", papers: []},
            {id: 2, state: "Approved", papers: []}
        ];
        jest.spyOn(lib, 'getIssues').mockImplementationOnce(() => {
            return Promise.resolve(issues);
        });
        const component = renderWithRouter(
            <Issues isAuthenticated={true} currentUser={currentUser}/>,
        );
        const testInstance = component.root;
        expect(testInstance).toBeTruthy();
    });
});