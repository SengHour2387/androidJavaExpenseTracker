package com.hourdex.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.hourdex.expensetracker.database.tables.CategoryTable;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private MainActivity mainActivity;
    private List<CategoryTable> categories = new ArrayList<>();

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(categories, this::showEditCategoryDialog);
        recyclerView.setAdapter(adapter);

        loadCategories();

        return view;
    }

    private void loadCategories() {
        if (mainActivity == null)
            return;

        new Thread(() -> {
            List<CategoryTable> list = mainActivity.getCategoryDao().getAll();
            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    categories.clear();
                    categories.addAll(list);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void showEditCategoryDialog(CategoryTable category) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.add_category_btm_modal);

        TextView title = bottomSheetDialog.findViewById(R.id.textView2);
        if (title != null)
            title.setText("Edit Category");

        Button btnUpdate = bottomSheetDialog.findViewById(R.id.btn_create);
        if (btnUpdate != null)
            btnUpdate.setText("Update");

        TextInputEditText categoryNameInput = bottomSheetDialog.findViewById(R.id.category_name_input);
        if (categoryNameInput != null) {
            categoryNameInput.setText(category.name);
        }

        btnUpdate.setOnClickListener(v -> {
            String newName = categoryNameInput.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Enter category name", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                mainActivity.getCategoryDao().updateCategory(category.id, newName);
                loadCategories();
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Category updated", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    });
                }
            }).start();
        });

        bottomSheetDialog.show();
    }
}