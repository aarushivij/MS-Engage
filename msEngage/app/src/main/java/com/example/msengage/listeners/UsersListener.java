package com.example.msengage.listeners;

import com.example.msengage.models.User;

public interface UsersListener {

    void initiateVideoCall(User user);

    void initiateAudioCall(User user);

    void onMultipleUsersAction(Boolean isMultipleUsersSelected);

    void initiateChat(User user);
}
