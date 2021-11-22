package com.example.joboui.utils;

public enum JobStatus {

    REQUESTED(0,"Requested"),
    ACCEPTED(1,"Accepted"),
    DECLINED(2,"Declined"),
    NEGOTIATING(3,"Negotiating"),
    BOOKED(4,"Booked");

    public int code;
    public String description;

    JobStatus(int code,String description) {
        this.description = description;
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public String getDescription() {
        return description;
    }

}
