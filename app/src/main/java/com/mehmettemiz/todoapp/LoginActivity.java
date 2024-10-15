package com.mehmettemiz.todoapp;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.credentials.Credential;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.mehmettemiz.todoapp.databinding.ActivityLoginBinding;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2;

    FirebaseAuth auth;
    GoogleSignInClient gsc;
    GoogleSignInOptions gso;
    ActivityLoginBinding binding;
    Boolean lang;
    ProgressDialog progressDialog;
    CallbackManager mCallbackManager;
    LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Facebook sdk başlat
        FacebookSdk.sdkInitialize(LoginActivity.this);

        mCallbackManager = CallbackManager.Factory.create();
         loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("failed");
            }
        });

        binding.errorText.setVisibility(View.GONE);

        // Sistem dilini kontrol et
        lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

        if (lang) {
            binding.userEmail.setHint("E-mail");
            binding.userPassword.setHint("Şifre");
            binding.loginButton.setText("Giriş");
            binding.registerText.setText("Hesabınız yok mu?");
            binding.registerButton.setText(" Kayıt Ol");
        }

        // Sistem temasının rengi
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);

        if (isDarkMode) {
            binding.userEmail.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.userPassword.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            binding.loginButton.setTextColor(getResources().getColor(R.color.white));
        }

        // Google Sign-In seçeneklerini yapılandır
        gso = new GoogleSignInOptions.Builder((GoogleSignInOptions.DEFAULT_SIGN_IN))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Google Sign-In istemcisini oluştur
        gsc = GoogleSignIn.getClient(this, gso);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        SignInButton signInButton = findViewById(R.id.signInButton);

        // Buton tıklanma olayını tanımla
        signInButton.setOnClickListener(v -> signIn());

        binding.loginButton.setOnClickListener(v -> checkUser());
    }

    // Kullanıcıyı kontrol et
    private void checkUser() {
        String email = binding.userEmail.getText().toString();
        String password = binding.userPassword.getText().toString();

        // Mail kontrolü için bir regex oluştur
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        if(email.isEmpty() || !email.matches(emailRegex)) {
            if (lang) {
                binding.userEmail.setError("Lütfen geçerli bir e-mail adresi girin.");
            } else {
                binding.userEmail.setError("Please enter a valid email address.");
            }
        } else if (password.isEmpty() || password.length() < 7) {
            if (lang) {
                binding.userPassword.setError("Şifre geçersiz!");
            } else {
                binding.userPassword.setError("Unvalid password!");
            }
        } else {
            progressDialog.setTitle("Login");
            progressDialog.setMessage("Please wait, app check your credentials");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        binding.errorText.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    // Google Sign-In işlemini başlat
    private void signIn() {
        // Google Sign-In işlemini başlat
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Google Sign-In işleminin sonucunu işle
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    // Google hesabını al
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("LoginActivity", "SignInWithCredential: success" + account.getEmail());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w("LoginActivity", "SignInWithCredential: failed", e);
                }
            } else {
                // Başarısız giriş işlemi
                Log.w("LoginActivity", "signInResult:failed", task.getException());
            }
        }
    }

    // Google hesabını kullanarak Firebase'e giriş yap
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Giriş başarılı, MainActivity'ye geç
                            Log.d("LoginActivity", "SignInWithCredential: run");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Giriş başarısız
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

}

