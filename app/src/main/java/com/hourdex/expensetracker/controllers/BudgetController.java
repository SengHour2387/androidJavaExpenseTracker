package com.hourdex.expensetracker.controllers;

import androidx.lifecycle.LiveData;

import com.github.mikephil.charting.data.LineData;
import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.database.daos.BudgetDao;
import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.List;

public class BudgetController {

    BudgetDao budgetDao;

    public BudgetController(MainActivity mainActivity) {

        this.budgetDao = mainActivity.getBudgetDao();
    }
    public BudgetController( BudgetDao budgetDao ) {
        this.budgetDao = budgetDao;
    }
    public List<BudgetTable> getAllBudget() {
        return budgetDao.getAll();
    }

    public BudgetTable getLastBudget() {
        return budgetDao.getLastBudget();
    }

    public void setBudget(BudgetTable budget) {
        budgetDao.newRecord(budget);
    }
}
