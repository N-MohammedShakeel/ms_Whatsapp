package com.example.ms_whatsapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.ms_whatsapp.MainActivity
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.SharedPrefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

//need to add dependency to use FirebaseMessagingService()
class FirebaseService : FirebaseMessagingService() {

    companion object {
        private const val REPLY_ACTION_ID = "REPLY_ACTION_ID"
        private const val KEY_REPLY_TEXT = "KEY_REPLY_TEXT"


        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt() // random is a maths function in kotlin


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)


        // to reply from the notification
        val remoteInput = RemoteInput.Builder(KEY_REPLY_TEXT).setLabel("Reply").build()

        // for reply action we also creating a intent
        val replyIntent = Intent(this, NotificationReply::class.java)

        // Create a PendingIntent for the reply action
        val replyPendingIntent = PendingIntent.getBroadcast(this, 0, replyIntent, PendingIntent.FLAG_MUTABLE)

        // Create a NotificationCompat.Action object for the reply action
        val replyAction = NotificationCompat.Action.Builder(R.drawable.reply, "Reply", replyPendingIntent).addRemoteInput(remoteInput).build()


        val sharedCustomPref = SharedPrefs(applicationContext)
        // if we have the notification id then we can use that id and send a reply on it
        sharedCustomPref.setIntValue("values", notificationID)


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(Html.fromHtml("<b>${message.data["title"]}</b>: ${message.data["message"]}")) // optinal , used for to modify tittle to be bold
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(replyAction)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

}