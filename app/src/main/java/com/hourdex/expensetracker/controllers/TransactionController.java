package com.hourdex.expensetracker.controllers;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.database.tables.BudgetTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.Date;

public class TransactionController {
    MainActivity mainActivity;

    public TransactionController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private boolean hasBudget() {
        try {
            BudgetTable budgetTable = mainActivity.getBudgetDao().getLastBudget();
            return budgetTable != null;

        }catch (Exception e) {
            return false;
        }
    }

    private boolean updateBudget(TransactionTable transaction) {
        try {
            BudgetTable budgetTable = mainActivity.getBudgetDao().getLastBudget();

            if(budgetTable == null){
                return false;
            }
            Log.d("testDB", "budgetTable.current_amount: " + budgetTable.current_amount + ", transaction.amount: " + transaction.amount );

            budgetTable.current_amount += transaction.amount;

            BudgetTable newBudgetTable = new BudgetTable(
                    budgetTable.current_amount,
                    budgetTable.last_init_amount
            );

            mainActivity.getBudgetDao().newRecord(newBudgetTable);
            return true;
        } catch (Exception e) {
            Log.d("testDB", "error updating budget: " + e);
            return  false;
        }
    }

    public boolean createTransaction(TransactionTable transaction) {
        if(!hasBudget() && transaction.amount < 0) {
            return false;
        }
        if(transaction.amount > 0 && !hasBudget()) {
            final BudgetController budgetController = new BudgetController(mainActivity);
            budgetController.setBudget(
                    new BudgetTable(
                            transaction.amount,
                            transaction.amount
                    )
            );
        }
        mainActivity.getTransactionDao().newTransaction(
                new TransactionTable(
                        transaction.label,
                        transaction.amount,
                        transaction.category_id,
                        transaction.description,
                        new Date()
                )
        );
        updateBudget(transaction);
        return true;
    }

}
