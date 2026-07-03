package br.com.financepro.financePro.common.exceptions.dto;

import java.util.Date;
import java.util.Objects;

public class ExceptionResponse {

    private String message;
    private String details;
    private Date timestamp;

    public  ExceptionResponse() {}

    public ExceptionResponse(String message, String details, Date timestamp) {
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionResponse that = (ExceptionResponse) o;
        return Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getDetails(), that.getDetails()) && Objects.equals(getTimestamp(), that.getTimestamp());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getMessage());
        result = 31 * result + Objects.hashCode(getDetails());
        result = 31 * result + Objects.hashCode(getTimestamp());
        return result;
    }
}