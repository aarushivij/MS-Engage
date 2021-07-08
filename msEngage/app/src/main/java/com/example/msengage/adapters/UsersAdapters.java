package com.example.msengage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msengage.R;
import com.example.msengage.listeners.UsersListener;
import com.example.msengage.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.UserViewHolder> {

    private List<User> users;
    private UsersListener usersListener;
    private List<User> selectedUsers;

    public UsersAdapters(List<User> users, UsersListener usersListener) {
        this.users = users;
        this.usersListener = usersListener;
        selectedUsers = new ArrayList<>();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
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
        ConstraintLayout userContainer;
        ImageView imageSelected;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textInitials = itemView.findViewById(R.id.textInitials);
            textUserName = itemView.findViewById(R.id.textUserName);
            textUserEmail = itemView.findViewById(R.id.textUserEmail);
            imageVideoCall = itemView.findViewById(R.id.imageVideoCall);
            imageAudioCall = itemView.findViewById(R.id.imageAudioCall);
            userContainer = itemView.findViewById(R.id.userContainer);
            imageSelected = itemView.findViewById(R.id.imageSelected);
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

            userContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (imageSelected.getVisibility() != View.VISIBLE) {
                        selectedUsers.add(user);
                        imageSelected.setVisibility(View.VISIBLE);
                        imageVideoCall.setVisibility(View.GONE);
                        imageAudioCall.setVisibility(View.GONE);
                        usersListener.onMultipleUsersAction(true);
                    }
                    return true;
                }
            });

            userContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageSelected.getVisibility() == View.VISIBLE) {
                        selectedUsers.remove(user);
                        imageSelected.setVisibility(View.GONE);
                        imageAudioCall.setVisibility(View.VISIBLE);
                        imageVideoCall.setVisibility(View.VISIBLE);
                        if (selectedUsers.size() == 0) {
                            usersListener.onMultipleUsersAction(false);
                        }
                    } else {
                        if (selectedUsers.size() > 0) {
                            selectedUsers.add(user);
                            imageSelected.setVisibility(View.VISIBLE);
                            imageVideoCall.setVisibility(View.GONE);
                            imageAudioCall.setVisibility(View.GONE);
                        }
                    }

                }
            });
        }

    }

}
