package com.example.arthur.arcboxv2;

/**
 * Created by Arthur on 05.12.16.
 */

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class SimpleFirechatInstanceIdService extends FirebaseInstanceIdService {


    private static final String CHAT_ENGAGE_TOPIC = "chat_engage";

    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance()
                .subscribeToTopic(CHAT_ENGAGE_TOPIC);
    }

}
