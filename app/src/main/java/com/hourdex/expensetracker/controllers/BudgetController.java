package com.hourdex.expensetracker.controllers;

import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.List;

public class BudgetController {

    MainActivity mainActivity;

    public BudgetController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public List<BudgetTable> getAllBudget() {
        return mainActivity.getBudgetDao().getAll();
    }
    public void setBudget(BudgetTable budget) {
        mainActivity.getBudgetDao().newRecord(budget);
    }
}
