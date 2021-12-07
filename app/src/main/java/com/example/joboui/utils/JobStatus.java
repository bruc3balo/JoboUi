package com.example.joboui.utils;

public enum JobStatus {

    REQUESTED(0,"Requested"),
    ACCEPTED(1,"Accepted"),
    DECLINED(2,"Declined"),
    NEGOTIATING(3,"Negotiating"),
    READY(4,"Ready"),
    PROGRESS(5,"Progress"),
    COMPLETED(6,"Completed"),
    CANCELLED(7,"Cancelled"),
    SERVICE_COMPLETE(8,"Service Complete"),
    CLIENT_COMPLETE(9,"Client Complete"),
    CLIENT_RATING(10,"Client Rating"),
    SERVICE_RATING(11,"Provider Rating"),
    SERVICE_CANCELLED_IN_PROGRESS(12,"Service provider cancelled in progress"),
    CLIENT_CANCELLED_IN_PROGRESS(13,"Client cancelled in progress"),
    SERVICE_REPORTED(14,"Service reported"),
    CLIENT_REPORTED(15,"Client reported"),
    RATING(16,"Rating");

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
