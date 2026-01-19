package com.hourdex.expensetracker.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.List;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budget_tbl")
    List<BudgetTable> getAll();

    //query for getting last record of budget
    @Query("SELECT * FROM budget_tbl ORDER BY id DESC LIMIT 1")
    BudgetTable getLastBudget();
    @Insert
    void newRecord(BudgetTable budgetTable);
}
