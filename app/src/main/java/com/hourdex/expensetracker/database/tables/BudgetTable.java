package com.hourdex.expensetracker.database.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_tbl")
public class BudgetTable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "current_amount")
    public String current_amount;
    @ColumnInfo(name = "init_amount")
    public String init_amount;
}
