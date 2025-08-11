package com.pahanaedu.bookshop.persistence.common.observer;

import com.pahanaedu.bookshop.business.user.model.User;


 // Observer interface for session events

public interface SessionObserver {

     // Called when user logs in

    void onUserLogin(User user, String sessionId);

     // Called when user logs out

    void onUserLogout(User user, String sessionId);

     //Called when session expires

    void onSessionExpired(String sessionId);

     // Called when session is created

    void onSessionCreated(String sessionId);
}