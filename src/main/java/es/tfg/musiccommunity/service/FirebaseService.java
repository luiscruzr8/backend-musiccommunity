package es.tfg.musiccommunity.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class FirebaseService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public String subscribeToTopic(String token, String topic) throws FirebaseMessagingException {
        firebaseMessaging.subscribeToTopic(Collections.singletonList(token), topic);
        return "Subscription successful";
    }

    public String unsubscribeFromTopic(String token, String topic) throws FirebaseMessagingException {
        firebaseMessaging.unsubscribeFromTopic(Collections.singletonList(token), topic);
        return "Unsubscription successful";
    }

    public String sendNotificationToTopic(String topic, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setTopic(topic)
                .build();

        return "Message sent successfully, response: " + firebaseMessaging.send(message);
    }

    public String sendNotificationToUser(String firebaseToken, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .putData("title", title)
                .putData("body", body)
                .setToken(firebaseToken)
                .build();

        return "Message sent successfully, response: " + firebaseMessaging.send(message);
    }
}
