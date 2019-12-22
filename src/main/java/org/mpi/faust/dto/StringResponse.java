package org.mpi.faust.dto;

public class StringResponse {
    private String message;

    public StringResponse(String message)
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
