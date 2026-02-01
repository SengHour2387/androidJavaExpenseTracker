package com.hourdex.expensetracker.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hourdex.expensetracker.database.tables.CategoryTable;

import java.util.List;
@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category_tbl WHERE 1")
    List<CategoryTable> getAll();

    @Query("SELECT * from category_tbl WHERE id = :id LIMIT 1")
    CategoryTable getCategory( int id );

    @Insert
    Long insertCategory(CategoryTable category);
}
