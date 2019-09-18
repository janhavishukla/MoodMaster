package io.github.pravinkanna.facedetectionapp;

import android.app.Application;
import android.content.ContentProvider;
import android.os.Build;

import com.google.firebase.FirebaseApp;

public class FaceDetection extends Application {

    public static final String RESULT_TEXT = "RESULT_TEXT";
    public static final String RESULT_DIALOG = "RESULT_DIALOGUE";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
