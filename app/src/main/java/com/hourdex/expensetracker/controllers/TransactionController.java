package com.hourdex.expensetracker.controllers;

import android.util.Log;

import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.database.tables.BudgetTable;
import com.hourdex.expensetracker.database.tables.TransactionTable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionController {

    private final MainActivity mainActivity;

    public TransactionController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean createTransaction(TransactionTable transaction) {

        BudgetTable budget =
                mainActivity.getBudgetDao().getLastBudget();

        // Expense but no budget → reject
        if (transaction.amount < 0 && budget == null) {
            Log.d("testDB", "No budget, expense rejected");
            return false;
        }

        // Income and no budget → create budget
        if (transaction.amount > 0 && budget == null) {
            budget = new BudgetTable(
                    transaction.amount,
                    transaction.amount
            );
            mainActivity.getBudgetDao().newRecord(budget);
        }

        // Insert transaction
        mainActivity.getTransactionDao().newTransaction(
                new TransactionTable(
                        transaction.label,
                        transaction.amount,
                        transaction.category_id,
                        transaction.description,
                        new Date()
                )
        );

        // Update budget
        if (budget != null) {
            mainActivity.getBudgetDao().newRecord(
                    new BudgetTable(
                            budget.current_amount + transaction.amount,
                            budget.last_init_amount
                    )
            );
        }
        return true;
    }

    public List<TransactionTable> getTop5() {
        List<TransactionTable> top5 = Collections.emptyList();


        return top5;
    }
}
