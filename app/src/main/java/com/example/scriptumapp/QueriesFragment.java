package com.example.scriptumapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QueriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QueriesFragment extends Fragment implements View.OnClickListener  {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button book_loan, book_exchange, book_gift, back;
    public QueriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QueriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QueriesFragment newInstance(String param1, String param2) {
        QueriesFragment fragment = new QueriesFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queries, container, false);

        book_loan = rootView.findViewById(R.id.book_loan);
        book_exchange = rootView.findViewById(R.id.book_exchange);
        book_gift = rootView.findViewById(R.id.gift_book);
        back = rootView.findViewById(R.id.back_queries);

        book_loan.setOnClickListener(this);
        book_exchange.setOnClickListener(this);
        book_gift.setOnClickListener(this);
        back.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v){

        if (v.getId() == R.id.book_loan) {
            //Loan
            Toast.makeText(getContext(), "Loan", Toast.LENGTH_SHORT).show();
            replaceFragment(new BookLoanFragment());
        } else if (v.getId() == R.id.book_exchange) {
            //Exchange
            Toast.makeText(getContext(), "Exchange", Toast.LENGTH_SHORT).show();
            replaceFragment(new BookLoanFragment());
        } else if (v.getId() == R.id.gift_book) {
            //Gift
            Toast.makeText(getContext(), "Gift", Toast.LENGTH_SHORT).show();
            replaceFragment(new GiftBookFragment());
        } else if (v.getId() == R.id.back) {
            //Exit
            Toast.makeText(getContext(), "Error Exit", Toast.LENGTH_SHORT).show();
            replaceFragment(new QueriesFragment());
        }
    }


    //cambiar de Fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getParentFragmentManager(); // Obtiene el FragmentManager del padre
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(this.getId(), fragment); // Reemplaza el fragmento actual con el nuevo fragmento
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}