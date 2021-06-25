package com.example.msengage.firebase;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.msengage.activities.IncomingCallActivity;
import com.example.msengage.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constants.REMOTE_MESSAGE_TYPE);

        if (type != null) {
            if (type.equals(Constants.REMOTE_MESSAGE_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
                intent.putExtra(Constants.REMOTE_MESSAGE_MEETING_TYPE, remoteMessage.getData().get(Constants.REMOTE_MESSAGE_MEETING_TYPE));
                intent.putExtra(Constants.KEY_FIRST_NAME, remoteMessage.getData().get(Constants.KEY_FIRST_NAME));
                intent.putExtra(Constants.KEY_LAST_NAME, remoteMessage.getData().get(Constants.KEY_LAST_NAME));
                intent.putExtra(Constants.KEY_EMAIL, remoteMessage.getData().get(Constants.KEY_EMAIL));
                intent.putExtra(Constants.REMOTE_MESSAGE_TOKEN_OF_SENDER, remoteMessage.getData().get(Constants.REMOTE_MESSAGE_TOKEN_OF_SENDER));
                intent.putExtra(Constants.REMOTE_MESSAGE_MEETING_ROOM, remoteMessage.getData().get(Constants.REMOTE_MESSAGE_MEETING_ROOM));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE)) {
                Intent intent = new Intent(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE);
                intent.putExtra(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE, remoteMessage.getData().get(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE));
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }
}
