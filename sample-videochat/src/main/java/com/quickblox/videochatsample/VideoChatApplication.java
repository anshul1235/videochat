package com.quickblox.videochatsample;

import android.app.Application;

import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

public class VideoChatApplication extends Application {


    private QBUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        // Set QuickBlox credentials here
    }

    public void setCurrentUser(int userId, String userPassword) {
        this.currentUser = new QBUser(userId);
        this.currentUser.setPassword(userPassword);
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }
}
