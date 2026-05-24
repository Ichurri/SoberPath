package com.santiago.soberpath.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.santiago.soberpath.R

class SoberPathFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: getString(R.string.notification_push_default_title)
        val body = message.notification?.body
            ?: message.data["message"].orEmpty()

        if (body.isNotBlank()) {
            NotificationHelper(applicationContext).showMessage(title, body)
        }
    }

    override fun onNewToken(token: String) {
        // Token can be sent to backend when available. Avoid logging sensitive tokens.
    }
}

