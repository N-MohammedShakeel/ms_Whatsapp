package com.example.ms_whatsapp.notifications.network

import com.example.ms_whatsapp.notifications.Constants.Companion.CONTENT_TYPE
import com.example.ms_whatsapp.notifications.Constants.Companion.SERVER_KEY
import com.example.ms_whatsapp.notifications.entity.PushNotification
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): retrofit2.Response<ResponseBody>
}