package com.hourdex.expensetracker;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.hourdex.expensetracker.controllers.BudgetController;
import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.ArrayList;
import java.util.Objects;

public class ListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int percentTage;
    private String mParam2;
    private TransactionAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    public ListFragment() {
        // Required empty public constructor
    }

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
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setProgress(percentTage);

        listView = view.findViewById(R.id.transaction_list);
        adapter = new TransactionAdapter(getContext(), new ArrayList<>());

        LinearLayout budgetContainer = view.findViewById(R.id.budget_container);
        budgetContainer.setOnClickListener(containerView->{
            final Intent intent = new Intent();
            intent.setClass(getContext(), BudgetActivity.class);
            startActivity(intent);
        });


        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();

        if (mainActivity != null) {
            mainActivity.getTransactionDao().getAll().observe(getViewLifecycleOwner(), transactionTables -> {
                adapter.clear();
                adapter.addAll(transactionTables);
                adapter.notifyDataSetChanged();

                transactionTables.forEach(t -> {
                    Log.d("testDB", "Transaction: id:" + t.id + ", label:" + t.label +
                            ", amount:" + t.amount + ", category_id:" + t.category_id);
                });
            });

            new Thread(()->{
                BudgetTable lastTable = mainActivity.getBudgetDao().getLastBudget();
                Log.d("testDB", "lastTable: " + lastTable.current_amount + ", " + lastTable.last_init_amount);

                percentTage = (int) (  lastTable.current_amount/lastTable.last_init_amount * 100) ;
                Log.d("testDB", "percentTage: " + percentTage);
                progressBar.setProgress(percentTage,true);
            }).start();



        }
    }
}