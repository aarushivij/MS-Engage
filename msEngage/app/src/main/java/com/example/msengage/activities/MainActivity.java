package com.example.msengage.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.msengage.R;
import com.example.msengage.adapters.UsersAdapters;
import com.example.msengage.listeners.UsersListener;
import com.example.msengage.models.User;
import com.example.msengage.utilities.Constants;
import com.example.msengage.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapters usersAdapters;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imageConference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());
        TextView textUserNameHeader = findViewById(R.id.text_user_name_header);
        textUserNameHeader.setText(String.format("%s %s",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));

        imageConference = findViewById(R.id.imageConference);

        // getting fcm token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            sendFCMTokenToDataBase(task.getResult());
                        }

                    }
                });

        TextView textSignOut = findViewById(R.id.textSignOut);
        textSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        //Setting up recycler view to show available users
        RecyclerView recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        textErrorMessage = findViewById(R.id.textErrorMessage);


        users = new ArrayList<>();
        usersAdapters = new UsersAdapters(users, this);
        recyclerViewUsers.setAdapter(usersAdapters);


        //To refresh list of available users on swiping up;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        getUsers();

        ImageView image_info_icon = findViewById(R.id.image_info_icon);
        Dialog dialog = new Dialog(this);
        image_info_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.help_dialog_layout);
                dialog.show();
            }
        });

    }

    //get list of users from firebase and updating the recycler view user adapter
    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        swipeRefreshLayout.setRefreshing(false);
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            users.clear();                                                           //So that on swiping, user list do not already have data. Else there will be repetition of users.
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (myUserId.equals(queryDocumentSnapshot.getId())) {
                                    continue;
                                }
                                String token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                if (token == null || token.trim().isEmpty()) {
                                    continue;
                                }
                                User user = new User();
                                user.firstName = queryDocumentSnapshot.getString(Constants.KEY_FIRST_NAME);
                                user.lastName = queryDocumentSnapshot.getString(Constants.KEY_LAST_NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                                user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                user.userId = queryDocumentSnapshot.getId();
                                users.add(user);
                            }
                            if (users.size() > 0) {
                                usersAdapters.notifyDataSetChanged();
                                textErrorMessage.setVisibility(View.GONE);
                            } else {
                                usersAdapters.notifyDataSetChanged();
                                textErrorMessage.setText(String.format("%s", "No users available"));
                                textErrorMessage.setVisibility(View.VISIBLE);
                            }

                        } else {
                            textErrorMessage.setText(String.format("%s", "No users available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }

                    }
                });
    }


    //Updating FCM token to FireStore database
    private void sendFCMTokenToDataBase(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, "Error: Unable to send token " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    //Signing out user and removing fcm token from its database
    private void signOut() {
        Toast.makeText(MainActivity.this, "Signing Out...", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                preferenceManager.clearPreferences();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Unable to sign out. Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //initiating video call and passing user's information to OutgoingCall Activity.
    @Override
    public void initiateVideoCall(User user) {

        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, user.firstName + " " + user.lastName + " is not available at the moment", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingCallActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioCall(User user) {

        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, user.firstName + " " + user.lastName + " is not available at the moment", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingCallActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }

    }



    @Override
    public void onMultipleUsersAction(Boolean isMultipleUsersSelected) {
        if (isMultipleUsersSelected) {
            imageConference.setVisibility(View.VISIBLE);
            imageConference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), OutgoingCallActivity.class);
                    intent.putExtra("selectedUsers", new Gson().toJson(usersAdapters.getSelectedUsers()));
                    intent.putExtra("type", "video");
                    intent.putExtra("isMultiple", true);
                    startActivity(intent);
                }
            });
        } else {
            imageConference.setVisibility(View.GONE);
        }
    }

    @Override
    public void initiateChat(User user) {

        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(MainActivity.this, user.firstName + " " + user.lastName + " is not available at the moment", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("receiverUid", user.userId);
            intent.putExtra("receiverName",user.firstName);
            startActivity(intent);
        }
    }
}