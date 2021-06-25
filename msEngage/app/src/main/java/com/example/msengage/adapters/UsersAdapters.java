package com.example.msengage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msengage.R;
import com.example.msengage.listeners.UsersListener;
import com.example.msengage.models.User;

import java.util.List;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.UserViewHolder> {

    private List<User> users;
    private UsersListener usersListener;

    public UsersAdapters(List<User> users, UsersListener usersListener) {
        this.users = users;
        this.usersListener = usersListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_user,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapters.UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textInitials, textUserName, textUserEmail;
        ImageView imageVideoCall, imageAudioCall;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.textInitials);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserEmail = itemView.findViewById(R.id.textUserEmail);
            imageVideoCall = itemView.findViewById(R.id.imageVideoCall);
            imageAudioCall = itemView.findViewById(R.id.imageAudioCall);
        }

        void setUserData(User user) {
            textInitials.setText(String.format("%s%s", user.firstName.charAt(0), user.lastName.charAt(0)));
            textUserName.setText(String.format("%s %s", user.firstName, user.lastName));
            textUserEmail.setText(user.email);
            imageAudioCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateAudioCall(user);
                }
            });

            imageVideoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateVideoCall(user);
                }
            });
        }

    }

}
