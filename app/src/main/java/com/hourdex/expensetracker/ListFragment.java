package com.hourdex.expensetracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int percentTage;
    private String mParam2;

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pTag Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(int pTag, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(pTag));
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            percentTage = Integer.parseInt(getArguments().getString(ARG_PARAM1));
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fetchAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_list, container, false);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setProgress(percentTage);
        fetchAll();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchAll();

        MainActivity mainActivity = (MainActivity) getActivity();

        assert mainActivity != null;
        mainActivity.getTransactionDao().getAll().observe(getViewLifecycleOwner(), transactionTables -> {
            transactionTables.forEach(t->{
                Log.d("testDB", "onCreateView: " +", id:"+ t.id +", label:"+ t.label + ", amount:"+ t.amount +", category_id:"+ t.category_id +", description:"+ t.description);
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAll();
    }

    private void fetchAll() {
        new Thread(()->{
            MainActivity mainActivity = (MainActivity) getActivity();
            Log.d("testDB", "onCreateView: " + mainActivity.getTransactionDao().getAll());
        });
    }
}