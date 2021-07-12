package com.example.msengage.utilities;

import java.util.HashMap;

public class Constants {

    //Firestore collection name
    public static final String KEY_COLLECTION_USERS = "users";

    // User id of signed in user
    public static final String KEY_USER_ID = "user_id";

    //Data keys in Firestore
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    //preference manager constants
    public static final String KEY_PREFERENCE_NAME = "msEngagePreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";

    //header constants
    public static final String REMOTE_MESSAGE_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MESSAGE_CONTENT_TYPE = "Content-Type";

    //custom data constants used in sending remote message
    public static final String REMOTE_MESSAGE_TYPE = "type";
    public static final String REMOTE_MESSAGE_INVITATION = "invitation";
    public static final String REMOTE_MESSAGE_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MESSAGE_TOKEN_OF_SENDER = "tokenOfSender";
    public static final String REMOTE_MESSAGE_DATA = "data";
    public static final String REMOTE_MESSAGE_REGISTRATION_IDS = "registration_ids";

    //for sending response message
    public static final String REMOTE_MESSAGE_INVITATION_RESPONSE = "invitationResponse";

    //information about call status
    public static final String REMOTE_MESSAGE_INVITATION_ACCEPTED = "accepted";
    public static final String REMOTE_MESSAGE_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MESSAGE_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MESSAGE_MEETING_ROOM = "meetingRoom";

    //chat constants
    public static final String KEY_CHAT_USER = "chatUser";
    public static final String KEY_CHATS = "chats";
    public static final String KEY_MESSAGES = "messages";


    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MESSAGE_AUTHORIZATION,
                "key=AAAA2SLpfPs:APA91bEhEuJKNeZkNAQJESzTTHAPyAgHWOChk95TX3uAo4hUg5YIlhItScoXavtzxNEH-jcLH7gXnuQcmkiykcmSsvf0T8TGPQF-a4TXaXF0pJW23fCySZcLQX7nB_W5ywiFJSKLrxoL "
        );
        headers.put(Constants.REMOTE_MESSAGE_CONTENT_TYPE, "application/json");
        return headers;
    }

}
