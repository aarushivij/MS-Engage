package com.example.msengage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.msengage.R;
import com.example.msengage.models.User;

import java.util.List;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.UserViewHolder> {

    private List<User> users;

    public UsersAdapters(List<User> users) {
        this.users = users;
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

    static class UserViewHolder extends RecyclerView.ViewHolder {
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
        }

    }

}
