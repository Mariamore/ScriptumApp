package com.example.scriptumapp;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookEditFragment extends Fragment {
    /*

    private String mParam1;
    private String mParam2;

    private EditText authorEditText_bookEdit, titleEditText_bookEdit, editorialEditText_bookEdit, yearEditText_bookEdit, statusEditText_bookEdit;
    private ImageView imageBook_bookEdit;
    private Button button_bookEdit;

    private Book book;
    private FirebaseFirestore db;
    StorageReference stRe;
    private String idUser;

    private Uri imageUri;
    //private String bookId;

    public BookEditFragment() {
        // Required empty public constructor
    }

    public static BookEditFragment newInstance(Book book, String idUser, String bookId) {
        BookEditFragment fragment = new BookEditFragment();
        Bundle args = new Bundle();
        args.putParcelable("book", book); // pasar datos entre fragments
        args.putString("idUser", idUser);
        args.putString("bookId", bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable("book");
            idUser = getArguments().getString("idUser");


        }
        // Instanciamos la base de datos
        db = FirebaseFirestore.getInstance();
        stRe = FirebaseStorage.getInstance().getReference();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_edit, container, false);


        authorEditText_bookEdit = rootView.findViewById(R.id.authorEditText_bookEdit);
        titleEditText_bookEdit  = rootView.findViewById(R.id.titleEditText_bookEdit);
        yearEditText_bookEdit = rootView.findViewById(R.id.yearEditText_bookEdit);
        imageBook_bookEdit = rootView.findViewById(R.id.rectangle_bookEditImage);
        button_bookEdit = rootView.findViewById(R.id.button_bookEdit);
        statusEditText_bookEdit = rootView.findViewById(R.id.currentStatusEditText_bookEdit);
        editorialEditText_bookEdit= rootView.findViewById(R.id.editorialEditText_bookEdit);

        //Extraemos los datos del libro
        authorEditText_bookEdit.setText(book.getAuthor());
        titleEditText_bookEdit.setText(book.getTitle());
        //yearEditText_bookEdit.setText(book.get);
        statusEditText_bookEdit.setText(book.getStatus());
        Picasso.get().load(book.getPhoto()).into(imageBook_bookEdit);
        //editorialEditText_bookEdit.setText(book.get);

        button_bookEdit.setOnClickListener(new View.OnClickListener() {
            private String bookId;

            @Override
            public void onClick(View v) {

                book.setTitle(titleEditText_bookEdit.getText().toString());
                book.setAuthor(authorEditText_bookEdit.getText().toString());
                book.setStatus(statusEditText_bookEdit.getText().toString());
                //Faltan parametros del libro

                //Lógica base datos
                //String bookId = book.getTitle(); //usamos el titulo como campo
                String bookId = this.bookId;
                Map<String, Object> data = new HashMap<>();
                data.put("title", book.getTitle());
                data.put("author", book.getAuthor());
                //data.put("year", book.getYear());
                data.put("status", book.getStatus());
                //data.put("editorial", book.getEditorial());
                data.put("photo", book.getPhoto());

                db.collection("users").document(idUser).collection("gift").document(bookId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                uploadPhoto(bookId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error saving book data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //Selecinar Imagen nueva en la edicion
        imageBook_bookEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //selecionamos la iamgen
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, COD_SEL_IMAGE);//deprecado
            }
        });

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == COD_SEL_IMAGE && data != null) {
            imageUri = data.getData();
            imageBook_bookEdit.setImageURI(imageUri);
        }
    }

public void uploadPhoto(String TitleBookId){
        
        StorageReference imageRef = stRe.child("users/" + idUser + "/gift/" +  + ".jpg");
        imageRef.putFile(imageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();

                    // Actualizar la URL de la imagen en Firestore
                    db.collection("users").document(idUser).collection("gift").document(bookId)
                            .update("photo", imageUrl)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Book and image updated successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error updating photo URL in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error getting download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


}
*/

}




