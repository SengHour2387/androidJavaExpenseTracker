package com.hourdex.expensetracker.database.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "transaction_tbl")
public class TransactionTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "category_id")
    public int category_id;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "date")
    public Date date;


    @Ignore
    public TransactionTable() {}

    public TransactionTable(String label, double amount, int category_id, String description, Date date) {
        this.label = label;
        this.amount = amount;
        this.category_id = category_id;
        this.description = description;
        this.date = date;
    }
}