import {API_BASE_URL, POLL_LIST_SIZE, ACCESS_TOKEN} from '../constants';

const request = (options) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
    });

    if (localStorage.getItem(ACCESS_TOKEN)) {
        headers.append('Authorization', 'Bearer ' + localStorage.getItem(ACCESS_TOKEN))
    }

    const defaults = {headers: headers};
    options = Object.assign({}, defaults, options);

    return fetch(options.url, options)
        .then(response => {
                console.log(response);
                if (response.json) {
                    return response.json().then(json => {
                        if (!response.ok) {
                            return Promise.reject(json);
                        }
                        return json;
                    }).catch(console.log);
                }

                return null;
            }
        );
};

export function getIssues() {
    return request({
        url: API_BASE_URL + "/treasury/issues",
        method: 'GET'
    });
}

export function createIssue(issue) {
    return request({
        url: API_BASE_URL + "/treasury/issues",
        method: 'POST',
        body: JSON.stringify(issue)
    });
}

export function removeIssue(id) {
    return request({
        url: API_BASE_URL + "/treasury/issues/" + id,
        method: 'DELETE'
    });
}

export function login(loginRequest) {
    return request({
        url: API_BASE_URL + "/auth/signin",
        method: 'POST',
        body: JSON.stringify(loginRequest)
    });
}

export function getCurrentUser() {
    if (!localStorage.getItem(ACCESS_TOKEN)) {
        return Promise.reject("No access token set.");
    }

    return request({
        url: API_BASE_URL + "/user/me",
        method: 'GET'
    });
}