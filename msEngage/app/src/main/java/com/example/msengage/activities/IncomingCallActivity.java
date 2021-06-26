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
import com.example.msengage.network.ApiClient;
import com.example.msengage.network.ApiService;
import com.example.msengage.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingCallActivity extends AppCompatActivity {

    private String callType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        ImageView imageCallType = findViewById(R.id.imageCallType);
        TextView textIncomingCall = findViewById(R.id.textIncomingCall);
        TextView textUserInitials = findViewById(R.id.textUserInitials);
        TextView textNameOfCallInitiator = findViewById(R.id.textNameOfCallInitiator);
        TextView textEmailOfCallInitiator = findViewById(R.id.textEmailOfCallInitiator);
        ImageView imageCallAccept = findViewById(R.id.imageCallAccept);
        ImageView imageCallReject = findViewById(R.id.imageCallReject);

        callType = getIntent().getStringExtra(Constants.REMOTE_MESSAGE_MEETING_TYPE);
        if (callType != null) {
            if (callType.equals("video")) {
                imageCallType.setImageResource(R.drawable.ic_video_call);
                textIncomingCall.setText(R.string.incoming_video_call);
            } else {
                imageCallType.setImageResource(R.drawable.ic_audio_call);
                textIncomingCall.setText(R.string.incoming_audio_call);
            }
        }


        String firstName = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        String lastName = getIntent().getStringExtra(Constants.KEY_LAST_NAME);
        String email = getIntent().getStringExtra(Constants.KEY_EMAIL);
        if (firstName != null && lastName != null) {
            textUserInitials.setText(String.format("%s%s", firstName.charAt(0), lastName.charAt(0)));
        }
        textNameOfCallInitiator.setText(String.format("%s %s", firstName, lastName));
        textEmailOfCallInitiator.setText(email);

        imageCallAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MESSAGE_INVITATION_ACCEPTED, getIntent().getStringExtra(Constants.REMOTE_MESSAGE_TOKEN_OF_SENDER));

            }
        });

        imageCallReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(Constants.REMOTE_MESSAGE_INVITATION_REJECTED, getIntent().getStringExtra(Constants.REMOTE_MESSAGE_TOKEN_OF_SENDER));
            }
        });
    }

    private void sendInvitationResponse(String responseType, String tokenOfSender) {
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(tokenOfSender);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MESSAGE_TYPE, Constants.REMOTE_MESSAGE_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE, responseType);
            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);
            sendRemoteMessage(body.toString(), responseType);


        } catch (Exception exception) {
            Toast.makeText(IncomingCallActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(),
                remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_ACCEPTED)) {
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverURL);
                            builder.setWelcomePageEnabled(false);
                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MESSAGE_MEETING_ROOM));
                            if (callType.equals("audio")) {
                                builder.setVideoMuted(true);
                            }
                            JitsiMeetActivity.launch(IncomingCallActivity.this, builder.build());
                            finish();

                        } catch (Exception exception) {
                            Toast.makeText(IncomingCallActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else {
                        Toast.makeText(IncomingCallActivity.this, "Call Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(IncomingCallActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                Toast.makeText(IncomingCallActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MESSAGE_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MESSAGE_INVITATION_CANCELLED)) {
                    Toast.makeText(IncomingCallActivity.this, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
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