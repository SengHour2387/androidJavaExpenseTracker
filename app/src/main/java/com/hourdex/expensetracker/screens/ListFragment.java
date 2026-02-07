package com.hourdex.expensetracker.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.R;
import com.hourdex.expensetracker.TransactionAdapter;
import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int percentTage;
    private String mParam2;
    private TransactionAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;

    private TextView amountView;
    private BudgetTable lastTable;
    private TextView budgetUpdate;
    private ImageView budgetIcon;

    private MainActivity mainActivity;

    public ListFragment() {}

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

        mainActivity = (MainActivity) getActivity();

        progressBar = view.findViewById(R.id.progressBar);
        budgetUpdate = view.findViewById(R.id.budget_change);
        progressBar.setProgress(percentTage);
        amountView = view.findViewById(R.id.amount_compare);
        budgetIcon = view.findViewById(R.id.trend_icon);

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
        if (mainActivity != null) {
            mainActivity.getTransactionDao().getAll().observe(getViewLifecycleOwner(), transactionTables -> {
                adapter.clear();
                adapter.addAll(transactionTables);
                adapter.notifyDataSetChanged();
            });
            setProgressBarView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            setProgressBarView();
        }
    }

    private void setProgressBarView() {
        new Thread(()->{
            lastTable = mainActivity.getBudgetDao().getLastBudget();
            if (lastTable != null) {
                percentTage = (int) (  lastTable.current_amount/lastTable.last_init_amount * 100) ;
                Log.d("testDB", "percentTage: " + percentTage);
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setProgress(percentTage, true);

                        double updateAmount =  lastTable.current_amount - lastTable.last_init_amount;

                        if(updateAmount<0) {
                            budgetUpdate.setTextColor(0xFFBB0000);
                            budgetUpdate.setText(String.format("%.1f$", updateAmount));
                            budgetIcon.setImageResource(R.drawable.trend_down);
                            budgetIcon.setScaleX(2f);
                            budgetIcon.setScaleY(2f);
                        } else {
                            budgetUpdate.setTextColor(0xFF00BB00);
                            budgetUpdate.setText(String.format( "+%.1f$", updateAmount));
                            budgetIcon.setImageResource(R.drawable.trend_up);
                            budgetIcon.setScaleX(2f);
                            budgetIcon.setScaleY(2f);
                        }
                        amountView.setText(String.format("%.1f$/%.1f$", lastTable.current_amount, lastTable.last_init_amount));
                    });
                }
            }
        }).start();
    }
}
