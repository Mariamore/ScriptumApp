package com.example.scriptumapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.scriptumapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<List<String>> imageUrls = new ArrayList<>();
    private List<List<String>> imageUrl2 = new ArrayList<>();
    private List<String> listBooks = new ArrayList<>();
    private ImageCarouselAdapter adapter;
     private ImageCarouselAdapter2 adapter2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ImageCarouselAdapter(imageUrls);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        fetchLatestImagesFromFirestore();

        adapter2 = new ImageCarouselAdapter2(getContext(), imageUrl2);
        binding.viewPager2.setAdapter(adapter2);
        binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        fetchLatestImagesFromFirestore2();

    }



    private void fetchLatestImagesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("booksData")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(9)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> urls = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("photo");
                            if (imageUrl != null) {
                                urls.add(imageUrl);
                                if (urls.size() == 3) {
                                    imageUrls.add(urls);
                                    urls = new ArrayList<>(); //Reiniciar para el próximo grupo de imágenes
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents.", task.getException());
                    }
                });

    }

    private void fetchLatestImagesFromFirestore2() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("booksData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            Collections.shuffle(documents); // Mezclar los documento, aleatorio

                            List<String> urls = new ArrayList<>();
                            //contador
                            int count = 0;
                            for (DocumentSnapshot document : documents) {
                                String imageUrl = document.getString("photo");
                                String bookId = document.getString("bookId");
                                if (imageUrl != null) {
                                    urls.add(imageUrl);
                                    listBooks.add(bookId); // Añadir el ID del libro a la lista
                                    count++;
                                    if (urls.size() == 3) {
                                        imageUrl2.add(urls); // Añadir a imageUrl2
                                        urls = new ArrayList<>(); //reinicamos imagenes nuevas
                                    }
                                    if (count >= 9) {
                                        break; //salimos del bucle
                                    }
                                }
                            }
                            adapter2.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}