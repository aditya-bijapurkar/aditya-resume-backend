package com.example.aditya_resume_backend.constants;

public class EmailConstants {

    private EmailConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String IST = "Asia/Kolkata";

    public static final String SCHEDULE_TIME = "schedule_time";
    public static final String SCHEDULE_DATE = "schedule_date";

    public static final String REQUEST_TEMPLATE_FILE = "request_meet_schedule.ftl";
    public static final String REQUEST_SUBJECT = "Request for meeting";

    public static final String INITIATE_TEMPLATE_FILE = "initiate_meet_schedule.ftl";
    public static final String INITIATE_SUBJECT = "Meeting schedule request initiated successfully!";

    public static final String ACCEPT_TEMPLATE_FILE = "accept_meet_schedule.ftl";
    public static final String ACCEPT_SUBJECT = "Meeting schedule has been accepted!";

    public static final String REJECT_TEMPLATE_FILE = "reject_meet_schedule.ftl";
    public static final String REJECT_SUBJECT = "Meeting schedule is rejected :(";

    public static final String NEW_CONTACT_MAIL = "USER %S CONTACTED FOR: %s";
    public static final String COPY_OF = "COPY OF: %s";

}
