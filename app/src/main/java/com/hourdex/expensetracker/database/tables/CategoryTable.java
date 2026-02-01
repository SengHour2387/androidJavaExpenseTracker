package com.hourdex.expensetracker.database.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_tbl")
public class CategoryTable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;
    @Ignore
    public CategoryTable( String name ) {
        this.name = name;
    }
    public CategoryTable() {}
}
