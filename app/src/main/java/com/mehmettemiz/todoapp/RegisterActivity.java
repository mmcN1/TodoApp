package com.mehmettemiz.todoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mehmettemiz.todoapp.databinding.ActivityRegisterBinding;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    Boolean lang;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Sistem dilini kontrol et
        lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

        // Sistem temasının rengi
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        if (isDarkMode) {
            binding.userName.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.email.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.password.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.signUpButton.setTextColor(getResources().getColor(R.color.white));
        }

        if (lang) {
            binding.userName.setHint("Kullanıcı Adı");
            binding.email.setHint("E-mail");
            binding.password.setHint("Şifre");
            binding.signUpButton.setText("Kayıt");
        }

        binding.signUpButton.setOnClickListener(v -> checkCredentials());

    }

    private void checkCredentials() {
        String userName = binding.userName.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        // Mail kontrolü için bir regex oluştur
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        // Kullanıcı adı kontrolü
        if (userName.isEmpty() || userName.length() < 3) {
            if (lang) {
                showError(binding.userName, "Lütfen en az 3 karakterden oluşan bir kullanıcı adı girin.");
            } else {
                showError(binding.userName, "Please enter a user name with at least 3 characters.");
            }
        }
        // Email kontrolü
        else if (email.isEmpty() || !email.matches(emailRegex)) {
            if (lang) {
                showError(binding.email, "Lütfen geçerli bir e-mail adresi girin.");
            } else {
                showError(binding.email, "Please enter a valid email address.");
            }
        }
        // Şifre kontrolü
        else if (password.isEmpty() || password.length() < 7) {
            if (lang) {
                showError(binding.password, "Lütfen en az 7 karakterden oluşan bir şifre girin.");
            } else {
                showError(binding.password, "Please enter a password with at least 7 characters.");
            }
        } else {
            progressDialog.setTitle("Registeration");
            progressDialog.setMessage("Please wait, app check your credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                auth.getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(userName).build());
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
            } else {
                    Toast.makeText(RegisterActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
        });
        }
    }

    private void showError(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();
    }
}