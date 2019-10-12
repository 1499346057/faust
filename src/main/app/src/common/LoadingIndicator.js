import React from 'react';

import { Spinner } from 'reactstrap';

function LoadingIndicator(props) {
    return (
        <Spinner type="grow" color="primary" />
    );
}

export default LoadingIndicator;