package com.example.msengage.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msengage.R;
import com.example.msengage.adapters.MessagesAdapter;
import com.example.msengage.firebase.MessagingService;
import com.example.msengage.models.Messages;
import com.example.msengage.utilities.Constants;
import com.example.msengage.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    String receiverUid, receiverName, senderUid;
    PreferenceManager preferenceManager;
    TextView receiverNameChat;
    EditText editTextMessage;
    ImageView sendBtn;
    FirebaseDatabase database;
    String senderRoom, receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;
    MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        preferenceManager = new PreferenceManager(getApplicationContext());
        receiverUid = getIntent().getStringExtra("receiverUid");
        receiverName = getIntent().getStringExtra("receiverName");
        senderUid = preferenceManager.getString(Constants.KEY_USER_ID);

        receiverNameChat = findViewById(R.id.receiverNameChat);
        receiverNameChat.setText(receiverName);

        sendBtn = findViewById(R.id.sendBtn);
        editTextMessage = findViewById(R.id.editTextMessage);

        senderRoom = senderUid+receiverUid;
        receiverRoom = receiverUid+senderUid;

        messagesArrayList = new ArrayList<>();

        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference chatReference = database.getReference().child(Constants.KEY_CHATS).child(senderRoom).child(Constants.KEY_MESSAGES);


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if(message.isEmpty())
                {
                    Toast.makeText(ChatActivity.this,"Please enter valid message",Toast.LENGTH_SHORT).show();
                    return;
                }

                editTextMessage.setText("");
                Date date = new Date();

                Messages messages = new Messages(message,senderUid,date.getTime());
                database = FirebaseDatabase.getInstance();
                database.getReference().child(Constants.KEY_CHATS)
                        .child(senderRoom)
                        .child(Constants.KEY_MESSAGES)
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        database.getReference().child(Constants.KEY_CHATS)
                                .child(receiverRoom)
                                .child(Constants.KEY_MESSAGES)
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                            }
                        });
                    }
                });

            }
        });




    }
}