package com.example.chatappwithfirebase.Notification;

import com.example.chatappwithfirebase.Notification.Response;
import com.example.chatappwithfirebase.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
        {
               "Content-Type:application/json",
                "Authorization:key=AAAAYrpOIzo:APA91bGNRTzCub2tq1B2WlL7p3utXr0HgqC70jBr2Vt_9x8c8Q4o1fUGOzrHMiM9qFXm2bi6tLrfQrtdC9Xgg8TuA97eoxx-zcItkkGCH4APB4lwn8jDs5lEYMDVKk8fAItjgeHHP4fW"
        }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
