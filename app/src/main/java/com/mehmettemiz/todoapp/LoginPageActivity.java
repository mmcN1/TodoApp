package com.mehmettemiz.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPageActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

         textView = findViewById(R.id.appName);
         textView2 = findViewById(R.id.appName1);


        textView.setTranslationX(-200);
        textView2.setTranslationX(200);
        textView2.setRotation(360);
        textView.animate().setDuration(2000).translationX(0).start();
        textView2.animate().setDuration(2000).translationX(0).rotation(0).start();

        // Firebase kullanıcı kontrolü
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Kullanıcı giriş yapmışsa MainActivity'ye, değilse LoginActivity'ye geçiş yap
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (user != null) {
                startActivity(new Intent(LoginPageActivity.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginPageActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);

    }
}