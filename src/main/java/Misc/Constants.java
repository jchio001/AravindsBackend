package Misc;

public class Constants {
    //id (Sequence)
    //name (32 characters)
    //about_me (1024 characters)
    //village (32 characters)
    //zip_code (int, 8 digits)
    //phone_number (16 characters)
    //email (32 characters)

    public static final int BAD_REQUEST = 400;
    public static final int UNATHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    public static final String READ_BODY_FAIL = "Failed to process the request body.";
    public static final String BAD_BODY_MESSAGE = "Request body wasn't a valid JSON.";
    public static final String DB_CONNECTION_FAIL = "Unable to connect to database.";

    //KEYS
    public static final String NAME = "name";
    public static final String ABOUT_ME = "about_me";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String VILLAGE = "village";
    public static final String ZIP_CODE = "zip_code";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    //CONN KEYS
    public static final String REQ_ID = "requester_id";
    public static final String TARGET_ID = "target_id";
    public static final String STATUS = "status";

    //status strings
    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";

    //count string
    public static final String COUNT = "count";

    //get profile status. Rel = relative, as in their profile's status relative to yours (the requester)
    public static final String REL_UNCONNECTED = "Unconnected";
    public static final String REL_PENDING = "Pending";
    public static final String REL_AWAITING_YOUR_APPROVAL = "Awaiting your approval";
    public static final String REL_ACCEPTED = "Accepted";
    public static final String REL_REJECTED = "Rejected";
    public static final String REL_REJECTED_BY_YOU = "Rejected by you";

}
