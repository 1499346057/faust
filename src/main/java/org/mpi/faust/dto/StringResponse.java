package org.mpi.faust.dto;

import lombok.*;

@Data
public class StringResponse {
    private String message;

    public StringResponse(String message)
    {
        this.setMessage(message);
    }

}
