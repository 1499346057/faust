package org.mpi.faust.web;

public class StringResponse {
    private String message;

    StringResponse(String message)
    {
        this.setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
