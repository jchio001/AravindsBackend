/**
 * Created by alexanderchiou on 12/24/15.
 */
public class Constants {
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

    //status strings
    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String REJECTED = "REJECTED";
}
