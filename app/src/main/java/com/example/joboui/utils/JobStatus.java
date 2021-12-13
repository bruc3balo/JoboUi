package com.example.joboui.utils;

public enum JobStatus {

    REQUESTED(0,"Requested"),
    ACCEPTED(1,"Accepted"),
    DECLINED(2,"Declined"),
    NEGOTIATING(3,"Negotiating"),
    READY(4,"Ready"),
    PROGRESS(5,"Progress"),
    COMPLETED(6,"Completed"), //completed not tested
    CANCELLED(7,"Cancelled"), //cancelled not tested
    SERVICE_COMPLETE(8,"Service Complete"),
    CLIENT_COMPLETE(9,"Client Complete"),
    CLIENT_RATING(10,"Client Rating"), //client rating not tested
    SERVICE_RATING(11,"Provider Rating"), //provider rating not tested
    SERVICE_CANCELLED_IN_PROGRESS(12,"Service provider cancelled in progress"),
    CLIENT_CANCELLED_IN_PROGRESS(13,"Client cancelled in progress"),
    SERVICE_REPORTED(14,"Service reported"), // hidden
    CLIENT_REPORTED(15,"Client reported"), //hidden
    RATING(16,"Rating"),
    PAYING(17,"Paying");

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
