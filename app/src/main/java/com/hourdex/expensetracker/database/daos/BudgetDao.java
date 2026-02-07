package com.hourdex.expensetracker.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hourdex.expensetracker.database.tables.BudgetTable;

import java.util.List;

@Dao
public interface BudgetDao {

    @Query("SELECT * FROM budget_tbl")
    List<BudgetTable> getAll();

    @Query("SELECT * FROM budget_tbl ORDER BY id DESC LIMIT 1")
    BudgetTable getLastBudget();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void newRecord(BudgetTable budgetTable);
}
