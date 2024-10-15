package com.mehmettemiz.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mehmettemiz.todoapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    Boolean lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
replaceFragment(new NotesFragment());

// Sistemin Türkçe dilinde olup olmadığını kontrol et
lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

if (lang) {
    binding.bottomNavigation.getMenu().getItem(0).setTitle("Notlar");
    binding.bottomNavigation.getMenu().getItem(1).setTitle("Hatırlatıcılar");
} else {
    binding.bottomNavigation.getMenu().getItem(0).setTitle("Notes");
    binding.bottomNavigation.getMenu().getItem(1).setTitle("Reminders");
}

// Buton tıklamalarını dinle
binding.bottomNavigation.setOnItemSelectedListener(item -> {
   switch (item.getItemId()) {
       case R.id.page_home:
           replaceFragment(new NotesFragment());
           break;
           case R.id.page_reminders:
               replaceFragment(new RemindersFragment());
               break;
   }

    return true;
        });


    }

    // Fragment değiştirme işlemi
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Add butonuna tıklama işlemi
    public void add(View view) {
        Intent intent = new Intent(MainActivity.this, TodoEditActivity.class);
        intent.putExtra("info", "new");
        startActivity(intent);

    }
}