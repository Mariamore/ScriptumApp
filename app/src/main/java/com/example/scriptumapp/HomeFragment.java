package com.example.scriptumapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.scriptumapp.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements ImageCarouselAdapter.OnItemClickListener, ImageCarouselAdapter2.OnItemClickListener{

    private FragmentHomeBinding binding;
    private List<List<String>> imageUrls = new ArrayList<>();
    private List<List<String>> imageUrls2 = new ArrayList<>();
    private ImageCarouselAdapter adapter;
    private ImageCarouselAdapter2 adapter2;
    private FirebaseFirestore db;
    private String bookId, idUser;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //hay que pensar si queremos que usuarios no autenticados puedan buscar o no
        if (user != null) {
            idUser = user.getUid();
            // Resto del código que utiliza el UID del usuario
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        adapter = new ImageCarouselAdapter(imageUrls, this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        adapter2 = new ImageCarouselAdapter2(imageUrls2, this);
        binding.viewPager2.setAdapter(adapter2);
        binding.viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

        fetchLatestImagesFromFirestore();
        fetchLatestImagesFromFirestore2();
    }

    private void fetchLatestImagesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("booksData")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(18)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> allUrls = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("photo");
                            String userString = document.getString("user");
                            // Solo mostramos libros nuevos de otros usuarios
                            if (imageUrl != null && !userString.equals(idUser)) {
                                allUrls.add(imageUrl);
                                if (allUrls.size() == 3) {
                                    imageUrls.add(allUrls);
                                    allUrls = new ArrayList<>(); // Reiniciamos para el próximo grupo de imágenes
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
        db.collection("recommendedBooks")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> urls = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("photo");
                            String userString = document.getString("user");
                            urls.add(imageUrl);
                            if (urls.size() == 3) {
                                imageUrls2.add(urls);
                                urls = new ArrayList<>(); // Reiniciar para el próximo grupo de imágenes
                            }
                        }
                        adapter2.notifyDataSetChanged();
                    } else {
                        Log.e("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Método para manejar el evento de clic en un elemento de la lista.
     * Abre el fragmento de la ficha del libro correspondiente al imageUrl proporcionado.
     *
     * @param imageUrl La URL de la imagen del libro seleccionado.
     */
    @Override
    public void onItemClick(String imageUrl) {
        openDetailFragment2(imageUrl);
    }

    /**
     * Método para abrir el fragmento de la ficha del libro.
     * Comprueba si el usuario está autenticado antes de abrir el fragmento.
     * Si el usuario está autenticado, busca el ID del libro asociado con la imagen proporcionada.
     * Luego, pasa el ID del libro al fragmento de l ficha del libro y realiza la transacción del fragmento.
     *
     * @param imageUrl La URL de la imagen del libro seleccionado.
     */
        private void openDetailFragment2(String imageUrl) {
            if(user!=null){
            db.collection("booksData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            if (document.contains("photo") && document.getString("photo").equals(imageUrl)) {
                                bookId = document.getId();
                            }

                        }
                    }
                    Log.d("bookid", bookId);
                    Bundle bundle = new Bundle();
                    bundle.putString("bookId", bookId);
                    BookInfoFragment fragment = new BookInfoFragment();
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = getParentFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });
            } else {
                negativeToast("You need to be logged in");

            }


        }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Método privado para mostrar un Toast de error con un mensaje específico.
     *
     * @param message El mensaje que se mostrará en el Toast de error.
     */
    private void negativeToast(String message) {
        // Usa inflater para inflar el diseño del toast
        LayoutInflater inflater = getLayoutInflater();
        // Infla el diseño personalizado del toast
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        // Busca el TextView dentro del diseño del toast
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        // Establece el mensaje proporcionado en el TextView
        txtMsg.setText(message);
        // Crea y muestra el toast
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}