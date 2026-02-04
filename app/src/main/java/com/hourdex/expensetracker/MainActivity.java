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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hourdex.expensetracker.database.ExpenseRoom;
import com.hourdex.expensetracker.database.daos.BudgetDao;
import com.hourdex.expensetracker.database.daos.CategoryDao;
import com.hourdex.expensetracker.database.daos.TransactionDao;
import com.hourdex.expensetracker.databinding.ActivityMainBinding;
import com.hourdex.expensetracker.screens.AnalyzeFragment;
import com.hourdex.expensetracker.screens.InsertFragment;
import com.hourdex.expensetracker.screens.ListFragment;
import com.hourdex.expensetracker.screens.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private TransactionDao transactionDao;
    private BudgetDao budgetDao;
    private CategoryDao categoryDao;

    public TransactionDao getTransactionDao() {
        return transactionDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public BudgetDao getBudgetDao() {
        return budgetDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ExpenseRoom room = Room.databaseBuilder(getApplicationContext(), ExpenseRoom.class, "expense_db")
                .build();

        transactionDao = room.transactionDao();
        budgetDao = room.budgetDao();
        categoryDao = room.categoryDao();

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
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, ListFragment.newInstance(35, null));
            fragmentTransaction.commit();
        }

        final FloatingActionButton addFab = binding.floatingActionButton;
        final BottomNavigationView bottomNavigationView = binding.bottomNavigationView;

        fragmentManager.addOnBackStackChangedListener( () ->{
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView);
            if (currentFragment instanceof InsertFragment) {
                binding.floatingActionButton.setVisibility(View.GONE);
                bottomNavigationView.animate().translationY(300f);
            } else {
                binding.floatingActionButton.setVisibility(View.VISIBLE);
                bottomNavigationView.animate().translationY(0f);
            }
        } );

        addFab.setOnClickListener( view -> {
            FragmentTransaction fragmentTransaction = fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_bottom,     // enter
                            R.anim.slide_out_bottom,    // exit
                            R.anim.slide_in_bottom,     // popEnter
                            R.anim.slide_out_bottom     // popBack
                            );
            fragmentTransaction.replace(R.id.fragmentContainerView, new InsertFragment());
            fragmentTransaction.addToBackStack("add_fragment");
            fragmentTransaction.commit();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int currentFragmentId = bottomNavigationView.getSelectedItemId();

            int id = item.getItemId();

            if(id != bottomNavigationView.getSelectedItemId()) {
                if(id == R.id.all_list) {;
                    binding.floatingActionButton.animate().translationX( 0f );
                    setCurrentTab(ListFragment.newInstance(35,null),
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.slide_in_right,     // enter
                                    R.anim.slide_out_left    // exit
                            )
                    );
                } else if (id == R.id.analyze) {
                    binding.floatingActionButton.animate().translationX( 300f );
                    if(currentFragmentId == R.id.all_list) {
                        setCurrentTab( new AnalyzeFragment(),
                                fragmentManager.beginTransaction().setCustomAnimations(
                                        R.anim.slide_in_left,     // enter
                                        R.anim.slide_out_right    // exit
                                ));
                    } else {setCurrentTab( new AnalyzeFragment(),
                                fragmentManager.beginTransaction().setCustomAnimations(
                                        R.anim.slide_in_right,     // enter
                                        R.anim.slide_out_left    // exit
                                ));
                    }

                } else if (id == R.id.settings) {
                    binding.floatingActionButton.animate().translationX( 300f );
                    setCurrentTab( new SettingsFragment() ,
                            fragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.slide_in_left,     // enter
                                    R.anim.slide_out_right    // exit
                            )
                    );
                }
            }
            return true;
        });


    }

    private void setCurrentTab( Fragment fragment, FragmentTransaction fragmentTransaction) {
        fragmentTransaction
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
}