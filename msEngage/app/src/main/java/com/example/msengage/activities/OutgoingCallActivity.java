package com.example.msengage.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.msengage.R;
import com.example.msengage.models.User;
import com.example.msengage.network.ApiClient;
import com.example.msengage.network.ApiService;
import com.example.msengage.utilities.Constants;
import com.example.msengage.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingCallActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String tokenOfSender = null;
    private String meetingRoom = null;
    private String callType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);

        ImageView imageCallType = findViewById(R.id.imageCallType);
        TextView textUserInitials = findViewById(R.id.textUserInitials);
        TextView textUserName = findViewById(R.id.textUserName);
        TextView textUserEmail = findViewById(R.id.textUserEmail);
        TextView textOutgoingCall = findViewById(R.id.textOutgoingCall);
        ImageView imageStopCall = findViewById(R.id.imageStopCall);

        preferenceManager = new PreferenceManager(getApplicationContext());

        callType = getIntent().getStringExtra("type");

        if (callType != null) {
            if (callType.equals("video")) {
                imageCallType.setImageResource(R.drawable.ic_video_call);
                textOutgoingCall.setText(R.string.video_call);
            } else {
                imageCallType.setImageResource(R.drawable.ic_audio_call);
                textOutgoingCall.setText(R.string.Audio_calling);
            }
        }

        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            textUserInitials.setText(String.format("%s%s", user.firstName.charAt(0), user.lastName.charAt(0)));
            textUserName.setText(String.format("%s %s", user.firstName, user.lastName));
            textUserEmail.setText(user.email);

        }


        imageStopCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    sendCancelInvitation(user.token);
                }
            }
        });

        //Getting token of the person who is initiating the call.
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    tokenOfSender = task.getResult();
                    if (callType != null && user != null) {
                        initiateCall(callType, user.token);
                    }
                }
            }
        });

    }

    //Initiating a call by sending remote message through FCM. Making remote message body.
    private void initiateCall(String callType, String tokenOfReceiver) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(tokenOfReceiver);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MESSAGE_TYPE, Constants.REMOTE_MESSAGE_INVITATION);
            data.put(Constants.REMOTE_MESSAGE_MEETING_TYPE, callType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            //So that receiver can use this token and send message back to its response, about call rejected/accepted.
            data.put(Constants.REMOTE_MESSAGE_TOKEN_OF_SENDER, tokenOfSender);

            meetingRoom = preferenceManager.getString(Constants.KEY_USER_ID) + " " + UUID.randomUUID().toString().substring(0, 5);
            data.put(Constants.REMOTE_MESSAGE_MEETING_ROOM, meetingRoom);

            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MESSAGE_INVITATION);

        } catch (Exception exception) {
            Toast.makeText(OutgoingCallActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

    }



    //Sending remote message to FCM with retrofit api.
    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),
                remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MESSAGE_INVITATION)) {
                        Toast.makeText(OutgoingCallActivity.this, "Invitation sent successfully", Toast.LENGTH_LONG).show();
                    } else if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_CANCELLED)) {
                        Toast.makeText(OutgoingCallActivity.this, "Invitation cancelled", Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else {
                    Toast.makeText(OutgoingCallActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Toast.makeText(OutgoingCallActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void sendCancelInvitation(String tokenOfReceiver) {
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(tokenOfReceiver);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MESSAGE_TYPE, Constants.REMOTE_MESSAGE_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE, Constants.REMOTE_MESSAGE_INVITATION_CANCELLED);
            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);
            sendRemoteMessage(body.toString(), Constants.REMOTE_MESSAGE_INVITATION_CANCELLED);


        } catch (Exception exception) {
            Toast.makeText(OutgoingCallActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_ACCEPTED)) {
                    try {
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(meetingRoom);
                        if (callType.equals("audio")) {
                            builder.setVideoMuted(true);
                        }
                        JitsiMeetActivity.launch(OutgoingCallActivity.this, builder.build());
                        finish();
                    } catch (Exception exception) {
                        Toast.makeText(OutgoingCallActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_REJECTED)) {
                    Toast.makeText(OutgoingCallActivity.this, "Call rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}