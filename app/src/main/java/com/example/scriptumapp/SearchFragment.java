package com.example.scriptumapp;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment  {
    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button searchButton, messageButton;
    private EditText searchEditText;
    private ListView searchListView;
    private List<String> titlesList = new ArrayList<String>();
    private List<String> authorsList = new ArrayList<>();
    private List<String> photosList = new ArrayList<>();
    private List<String> usersList = new ArrayList<>();
    private CollectionReference booksCollection;
    private Query query;
    private String queryText, titleBook, authorBook, photoBook, userBook;
    private FirebaseFirestore db;

    private RecyclerView mRecycler;

    private BookAdapterLoan bookAdapterLoan;
    private View rootView;



    private SearchView sW;

    //Añadimos binding para cargar otro fragment
    //FragmentSearchBinding binding;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    /*
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        // Estas dos líneas cambiarían el fragment
        //binding = FragmentSearchBinding.inflate(getLayoutInflater());
        //replaceFragment(new HomeFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        db = FirebaseFirestore.getInstance();
        booksCollection = db.collection("books");
        searchButton = rootView.findViewById((R.id.searchButton));
        //searchListView = rootView.findViewById(R.id.searchListView);
        sW = rootView.findViewById(R.id.search_bar);



        setUpRecyclerView();
        searchView();


        return rootView;
    }
    */
    /*
    private void setUpRecyclerView(){
        mRecycler = rootView.findViewById(R.id.recyclerSearch);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        query = db.collection("users");

    }



    private void searchView(){
        sW.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(s);
                return false;
            }
        });
    }

    public void  textSearch (String s){
        FirestoreRecyclerOptions<Book> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query.orderBy("title"), Book.class).build();
        bookAdapterLoan = new BookAdapterLoan(firestoreRecyclerOptions, this, getParentFragmentManager());
        bookAdapterLoan.startListening();
        mRecycler.setAdapter(bookAdapterLoan);

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}
*/
}