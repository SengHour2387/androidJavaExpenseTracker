package com.hourdex.expensetracker;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import androidx.viewbinding.ViewBindings;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hourdex.expensetracker.database.ExpenseRoom;
import com.hourdex.expensetracker.database.daos.TransactionDao;
import com.hourdex.expensetracker.databinding.ActivityMainBinding;
import com.hourdex.expensetracker.repos.transactionRepo;

public class MainActivity extends AppCompatActivity {

    private ExpenseRoom room;
    private TransactionDao transactionDao;


    public TransactionDao getTransactionDao() {
        return transactionDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        room = Room.databaseBuilder(getApplicationContext(), ExpenseRoom.class, "expense_db").build();

        final transactionRepo repo = new transactionRepo(room);
        transactionDao = room.transactionDao();

        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        setContentView(binding.main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, ListFragment.newInstance(35, null));
            fragmentTransaction.commit();
        }

        final FloatingActionButton addFab = binding.floatingActionButton;

        fragmentManager.addOnBackStackChangedListener( () ->{
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
            if (currentFragment instanceof InsertFragment) {
                binding.floatingActionButton.setVisibility(View.GONE);  // Hide when in InsertFragment
            } else {
                binding.floatingActionButton.setVisibility(View.VISIBLE);  // Show otherwise
            }
        } );

        addFab.setOnClickListener( view -> {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragmentContainerView, new InsertFragment());
            fragmentTransaction.addToBackStack("add_fragment");
            fragmentTransaction.commit();

        });
    }
}