package com.example.scriptumapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class SearchFragment extends Fragment implements BookAdapterSearch.OnMessageButtonClickListener {

    private Button searchButton, messageButton;
    private EditText searchEditText;
    private ListView searchListView;
    private List<String> titlesList = new ArrayList<String>();
    private List<String> authorsList = new ArrayList<>();
    private List<String> photosList = new ArrayList<>();
    private List<String> usersList = new ArrayList<>();
    private List<String> booksIdList = new ArrayList<>();
    private List<Integer> relevanceScores = new ArrayList<>();

    private List<String> savedTitlesList = new ArrayList<>();
    private List<String> savedAuthorsList = new ArrayList<>();
    private List<String> savedPhotosList = new ArrayList<>();
    private List<String> savedUsersList = new ArrayList<>();
    private List<String> savedBooksIdList = new ArrayList<>();
    private List<String> sortedBooksIds = new ArrayList<>();

    private CollectionReference booksCollection;
    private FirebaseUser user;
    private String queryText, titleBook, authorBook, photoBook, userBook, idUser, bookId;
    private FirebaseFirestore db;
    private View rootView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Añadir estas dos líneas siguientes
        //binding = FragmentSearchBinding.inflate(inflater, container, false);
        //return binding.getRoot();
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchEditText = rootView.findViewById(R.id.search_edit_text);
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        //hay que pensar si queremos que usuarios no autenticados puedan buscar o no
        if (user != null) {
            idUser = user.getUid();
            // Resto del código que utiliza el UID del usuario
        } /*else {
           idUser = "non_authenticated_user_id";

        }
        */
        booksCollection = db.collection("booksData");
        searchButton = rootView.findViewById((R.id.searchButton));
        searchListView = rootView.findViewById(R.id.searchListView);

        if (savedTitlesList.size() > 0) {
            BookAdapterSearch bookAdapter = new BookAdapterSearch(requireActivity(), savedTitlesList,
                    savedAuthorsList, savedPhotosList, savedUsersList, this);
            searchListView.setAdapter(bookAdapter);
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryText = searchEditText.getText().toString().toLowerCase().trim();
                searchAndUpdateUi(queryText);
            }
        });
        return rootView;
    }

    private void searchAndUpdateUi(String query){
        db.collection("booksData")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        titlesList.clear();
                        authorsList.clear();
                        photosList.clear();
                        usersList.clear();
                        booksIdList.clear();
                        relevanceScores.clear();

                        savedTitlesList.clear();
                        savedAuthorsList.clear();
                        savedPhotosList.clear();
                        savedUsersList.clear();
                        savedBooksIdList.clear();

                        sortedBooksIds.clear();

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                String titleString = doc.getString("title");
                                String authorString = doc.getString("author");
                                String userString = doc.getString("user");

                                String userId = doc.getString("user");


                                if (!userString.equals(idUser) && (containsAllWord(titleString, query) || containsAllWord(authorString, query))) {

                                    int relevanceScore = calculateRelevance(titleString, authorString, query);

                                    titlesList.add(doc.getString("title"));
                                    authorsList.add(doc.getString("author"));
                                    photosList.add(doc.getString("photo"));
                                    usersList.add(doc.getString("user"));
                                    booksIdList.add(doc.getId());
                                    relevanceScores.add(relevanceScore);
                                }
                            }
                        }



                        // Ordenamos los resultados en función de la puntuación de relevancia
                        int[] sortedIndices = IntStream.range(0, relevanceScores.size())
                                .boxed()
                                .sorted((i, j) -> Integer.compare(relevanceScores.get(j), relevanceScores.get(i)))
                                .mapToInt(ele -> ele)
                                .toArray();

                        List<String> sortedTitles = new ArrayList<>();
                        List<String> sortedAuthors = new ArrayList<>();
                        List<String> sortedPhotos = new ArrayList<>();
                        List<String> sortedUsers = new ArrayList<>();

                        for (int i : sortedIndices) {
                            sortedTitles.add(titlesList.get(i));
                            sortedAuthors.add(authorsList.get(i));
                            sortedPhotos.add(photosList.get(i));
                            sortedUsers.add(usersList.get(i));
                            sortedBooksIds.add(booksIdList.get(i));
                        }

                        savedTitlesList.addAll(sortedTitles);
                        savedAuthorsList.addAll(sortedAuthors);
                        savedPhotosList.addAll(sortedPhotos);
                        savedUsersList.addAll(sortedUsers);
                        savedBooksIdList.addAll(sortedBooksIds);

                        if (sortedTitles.isEmpty()) {
                            searchListView.setAdapter(null);
                            toastNoResultsFound();
                        } else {
                            BookAdapterSearch bookAdapter = new BookAdapterSearch(requireActivity(), sortedTitles, sortedAuthors, sortedPhotos, sortedUsers, SearchFragment.this);
                            searchListView.setAdapter(bookAdapter);
                        }
                    }
                });
    }

    // Método que verifica que estén todas las palabras en el documento
    private boolean containsAllWord(String text, String query) {
        if (text == null || query.isEmpty()) {
            return false;
        }
        String[] queryWords = query.toLowerCase().split("\\s+");
        for (String word : queryWords) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Calculamos la relevancia de los resultados con puntuación
    private int calculateRelevance(String title, String author, String query) {
        int score = 0;
        String[] queryWords = query.toLowerCase().split("\\s+");
        for (String word : queryWords) {
            if (title.toLowerCase().contains(word.toLowerCase())) {
                score++;
            }
            if (author.toLowerCase().contains(word.toLowerCase())) {
                score++;
            }
        }

        return score;
    }

    @Override
    public void onMessageButtonClick(int position) {
        //Enviamos el usuario al fragment de messagesChatFragment
        String textToSend = usersList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("userIdReceptor", textToSend);
        MessagesChatFragment messagesChatFragment = new MessagesChatFragment();
        messagesChatFragment.setArguments(bundle);
        replaceFragment(messagesChatFragment);
    }

    private void toastNoResultsFound() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout_fail,
                requireActivity().findViewById(R.id.toastLayoutFail));
        TextView txtMsg = layout.findViewById(R.id.toastMessage);
        txtMsg.setText(R.string.no_results_found);
        Toast toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    // Añadir este método para poder cambiar de fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }



    @Override
    public void onItemClick(int position) {
        String bookId = sortedBooksIds.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("bookId",bookId);
        // Crear una instancia de BookInfoFragment y asignar el Bundle
        BookInfoFragment fragment = new BookInfoFragment();
        fragment.setArguments(bundle);

        // Reemplazar el fragmento actual con BookInfoFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


}


