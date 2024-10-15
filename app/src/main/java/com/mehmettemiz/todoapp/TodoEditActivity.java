package com.mehmettemiz.todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mehmettemiz.todoapp.databinding.ActivityTodoEditBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TodoEditActivity extends AppCompatActivity {

private ActivityTodoEditBinding binding;
SQLiteDatabase database;
String info;
Intent intent;
String selectedId;
ImageView pinButton;
ImageView cancelButton;
boolean isPinned;
Boolean lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTodoEditBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Sistemin Türkçe olup olmadığını kontrol et
        lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

        if (lang) {
            binding.editTextText.setHint("Başlık");
            binding.editTextText2.setHint("Not");
        }else {
            binding.editTextText.setHint("Title");
            binding.editTextText2.setHint("Note");
        }

        pinButton = findViewById(R.id.pinButton);
        cancelButton = findViewById(R.id.cancelButton);

        // Firebase ve Firestore başlat
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        pinButton.setOnClickListener(v -> togglePinned());
        cancelButton.setOnClickListener(v -> {
                Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);
                });

        intent = getIntent();
        info = intent.getStringExtra("info");

        // Intent'ten veriyi al
        if (info.equals("new")) {
            binding.editTextText.setText("");
            binding.editTextText2.setText("");
        } else {
            String todoId = intent.getStringExtra("todoId");

            if (currentUser != null) {
                String userId = currentUser.getUid();

                db.collection("users").document(userId)
                        .collection("todos").document(todoId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String title = documentSnapshot.getString("title");
                                String note = documentSnapshot.getString("note");
                                isPinned = documentSnapshot.getBoolean("pinned") != null && documentSnapshot.getBoolean("pinned");

                                binding.editTextText.setText(title);
                                binding.editTextText2.setText(note);
                                pinButton.setSelected(isPinned);
                                pinButton.setColorFilter(isPinned ? getResources().getColor(R.color.secondary) : getResources().getColor(R.color.white));
                            }
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                        });
            }
        }

    }


    // Pin durumunu değiştir
    private void togglePinned() {
         isPinned = !isPinned;
        pinButton.setSelected(isPinned);  // Seçili durumu değiştir
        pinButton.setColorFilter(isPinned ? getResources().getColor(R.color.secondary) : getResources().getColor(R.color.white));


    }

    // Kaydet butonuna tıklandığında notu kaydet
    public void save(View view) {
        String title = binding.editTextText.getText().toString();
        String note = binding.editTextText2.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> todo = new HashMap<>();
            todo.put("title", title);
            todo.put("note", note);
            todo.put("pinned", isPinned);

            intent = getIntent();
            info = intent.getStringExtra("info");
            selectedId = intent.getStringExtra("todoId");

            if (title.isEmpty() && note.isEmpty()) {
                Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                if (info.equals("new")) {
                    db.collection("users").document(userId)
                            .collection("todos")
                            .add(todo)
                            .addOnSuccessListener(documentReference -> {
                                System.out.println("Not başarıyla kaydedildi: " + documentReference.getId());
                                Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Not kaydedilirken hata oluştu: " + e.getMessage());
                            });
                } else {
                    db.collection("users").document(userId)
                            .collection("todos").document(selectedId)
                            .update(todo)
                            .addOnSuccessListener(aVoid -> {
                                System.out.println("Not başarıyla güncellendi.");
                                Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            })
                            .addOnFailureListener(e -> {
                                System.out.println("Not güncellenirken hata oluştu: " + e.getMessage());
                            });
                }
            }
        }
    }

    // Geri tuşuna basıldığında notu kaydet
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title = binding.editTextText.getText().toString();
            String note = binding.editTextText2.getText().toString();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();
                Map<String, Object> todo = new HashMap<>();
                todo.put("title", title);
                todo.put("note", note);
                todo.put("pinned", isPinned);

                intent = getIntent();
                info = intent.getStringExtra("info");
                selectedId = intent.getStringExtra("todoId");

                if (title.isEmpty() && note.isEmpty()) {
                    Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    if (info.equals("new")) {
                        db.collection("users").document(userId)
                                .collection("todos")
                                .add(todo)
                                .addOnSuccessListener(documentReference -> {
                                    System.out.println("Not başarıyla kaydedildi: " + documentReference.getId());
                                    Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Not kaydedilirken hata oluştu: " + e.getMessage());
                                });
                    } else {
                        db.collection("users").document(userId)
                                .collection("todos").document(selectedId)
                                .update(todo)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Not başarıyla güncellendi.");
                                    Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Not güncellenirken hata oluştu: " + e.getMessage());
                                });
                    }
                }
            }
        }
        return true;
    }

    // Sil butonuna tıklandığında notu sil
public void delete(View view) {
    intent = getIntent();
    info = intent.getStringExtra("info");
    selectedId = intent.getStringExtra("todoId");

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    if (currentUser != null) {
        String userId = currentUser.getUid();


        db.collection("users").document(userId)
                .collection("todos").document(selectedId)
                .delete()
                .addOnSuccessListener(aVoid -> {

                    System.out.println("Not başarıyla silindi.");
                    Intent intent = new Intent(TodoEditActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {

                    System.out.println("Not silinirken hata oluştu: " + e.getMessage());
                });
    }
}

}