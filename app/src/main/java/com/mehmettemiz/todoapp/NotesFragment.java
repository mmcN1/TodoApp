package com.mehmettemiz.todoapp;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Color.alpha;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mehmettemiz.todoapp.databinding.FragmentNotesBinding;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private ArrayList<TodoItem> pinnedItemArrayList;
    private ArrayList<TodoItem> otherItemArrayList;
    private boolean isPinned;
    private ArrayList<TodoItem> filteredList;
    private ArrayList<TodoItem> mergedList;
    TodoAdapter pinnedAdapter;
    TodoAdapter otherAdapter;
    TodoAdapter searchAdapter;
    private FirebaseAuth auth;
    FirebaseUser currentUser;
    Boolean lang;


    private boolean changeImage = true; // Bu değişken ile resmi değiştireceğimizi kontrol edebiliriz.
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        pinnedItemArrayList = new ArrayList<>();
        otherItemArrayList = new ArrayList<>();
        filteredList = new ArrayList<>();
        mergedList = new ArrayList<>();

        // Sistemin Türkçe olup olmadığını kontrol et.
         lang = Locale.getDefault().getDisplayLanguage().equals("Türkçe");

getData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView profileImage = view.findViewById(R.id.profileImage);

        // Firebase kullanıcı bilgilerini al
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        binding.pinnedTextView.setVisibility(View.GONE);
        binding.otherTextView.setVisibility(View.GONE);


        // Profil resmi yükle
if (currentUser.getPhotoUrl() != null) {
    Glide.with(this).load(currentUser.getPhotoUrl().toString()).circleCrop().into(binding.profileImage);
    profileImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);

        }
    });
} else {
    binding.profileImage.setImageResource(R.drawable.user_name_foreground);
    profileImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);

        }
    });
}



        // LayoutManager'ı ayarla
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        StaggeredGridLayoutManager pinnedLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        binding.pinnedRecyclerView.setLayoutManager(pinnedLayoutManager);

        StaggeredGridLayoutManager findLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        binding.searchRecyclerView.setLayoutManager(findLayoutManager);


        // Adapter'ı ayarla
        pinnedAdapter = new TodoAdapter(pinnedItemArrayList);
        otherAdapter = new TodoAdapter(otherItemArrayList);
        searchAdapter = new TodoAdapter(filteredList);


        binding.recyclerView.setAdapter(otherAdapter);
        binding.pinnedRecyclerView.setAdapter(pinnedAdapter);
        binding.searchRecyclerView.setAdapter(searchAdapter);

        // Anismasyonları başlat
        binding.pinnedRecyclerView.setAlpha(0f);
        binding.pinnedRecyclerView.setTranslationY(11150);
        binding.pinnedRecyclerView.animate().setDuration(1000).alpha(1f).translationY(0).start();
        binding.recyclerView.setAlpha(0f);
        binding.recyclerView.setTranslationY(11150);
        binding.recyclerView.animate().setDuration(1000).alpha(1f).translationY(0).start();


        // Arama çubuğunu dinle
        binding.searchView.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (filteredList.isEmpty()) {
                    Toast.makeText(getContext(), "Not Found!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (lang) {
            binding.searchView.setHint("Ara");
            binding.pinnedTextView.setText("Sabitlenenler");
            binding.otherTextView.setText("Diğerleri");
            binding.searchBar.setHint("Ara");
        } else {
            binding.searchView.setHint("Search");
            binding.pinnedTextView.setText("Pinned");
            binding.otherTextView.setText("Others");
            binding.searchBar.setHint("Search");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

// Databaseden veri çekme işlemi
    private void getData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            pinnedItemArrayList.clear();
            otherItemArrayList.clear();

            db.collection("users").document(userId)
                    .collection("todos")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                String id = document.getId();
                                String name = document.getString("title");
                                String note = document.getString("note");
                                boolean isPinned = document.getBoolean("pinned") != null && document.getBoolean("pinned");

                                TodoItem todoItem = new TodoItem(name, id, note);

                                if (isPinned) {
                                    pinnedItemArrayList.add(todoItem);
                                } else {
                                    otherItemArrayList.add(todoItem);
                                }
                            }

                            pinnedAdapter.notifyDataSetChanged();
                            otherAdapter.notifyDataSetChanged();

                            if (!pinnedItemArrayList.isEmpty()) {
                                binding.pinnedTextView.setVisibility(View.VISIBLE);
                                    if (otherItemArrayList.isEmpty()) {
                                        binding.otherTextView.setVisibility(View.GONE);
                                    } else {
                                        binding.otherTextView.setVisibility(View.VISIBLE);
                                    }
                            } else {
                                binding.pinnedTextView.setVisibility(View.GONE);
                                binding.otherTextView.setVisibility(View.GONE);

                            }

                        } else {
                            binding.pinnedTextView.setVisibility(View.GONE);
                            binding.otherTextView.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }

    // Arama işlemi
    private void filter(String text) {
        filteredList.clear();

        mergedList.clear();
        mergedList.addAll(pinnedItemArrayList);
        mergedList.addAll(otherItemArrayList);

        for (TodoItem item : mergedList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

            searchAdapter.updateList(filteredList);

    }


}
