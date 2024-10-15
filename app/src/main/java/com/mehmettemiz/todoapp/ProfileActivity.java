package com.mehmettemiz.todoapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mehmettemiz.todoapp.databinding.ActivityProfileBinding;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth auth;
    GoogleSignInClient gsc;
    FirebaseUser currentUser;
    ActivityProfileBinding binding;
    Boolean lang;

    public static final int RC_SIGN_IN = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Sistem temasinin rengi
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        if (isDarkMode) {
            binding.signOutButton.setBackgroundColor(getResources().getColor(R.color.primary));
            binding.profileImageBg.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tertiary)));
        }

//Sisitemin Türkçe olup olmadığını kontrol et
        lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

        if (lang) {
            binding.signOutButton.setText("Çıkış Yap");
        } else {
            binding.signOutButton.setText("Sign Out");
        }

        // Firebase kullanıcı bilgilerini al
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

if (currentUser.getPhotoUrl() != null){
    Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(binding.profileImage);
    Glide.with(this).load(currentUser.getPhotoUrl()).into(binding.profileImageBg);

} else {
    binding.profileImage.setImageResource(R.drawable.user_name_foreground);
    binding.profileImageBg.setImageResource(R.drawable.user_name_foreground);
}
        binding.profileName.setText(currentUser.getDisplayName());
        binding.profileMail.setText(currentUser.getEmail());


        // Google hesabının bilgilerini al
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder((GoogleSignInOptions.DEFAULT_SIGN_IN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        Button signOutButton = findViewById(R.id.signOutButton);


        // Çıkış yapma işlemi
        signOutButton.setOnClickListener(v -> {
            auth.signOut();
            LoginManager.getInstance().logOut();
            gsc.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }
}