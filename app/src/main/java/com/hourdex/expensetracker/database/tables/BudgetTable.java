package com.hourdex.expensetracker.database.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_tbl")
public class BudgetTable {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    @ColumnInfo(name = "current_amount")
    public double current_amount;
    @ColumnInfo(name = "last_init_amount")
    public double last_init_amount;

    public BudgetTable() {}

    @Ignore
    public BudgetTable(double current_amount, double last_init_amount) {
        this.current_amount = current_amount;
        this.last_init_amount = last_init_amount;
    }
}
