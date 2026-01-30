package com.hourdex.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.hourdex.expensetracker.database.ExpenseRoom;
import com.hourdex.expensetracker.database.daos.BudgetDao;
import com.hourdex.expensetracker.database.tables.BudgetTable;
import com.hourdex.expensetracker.databinding.ActivityBudgetBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BudgetActivity extends AppCompatActivity {

    private LineChart budgetLineChart;

    private List<String> xValues = new ArrayList<>();
    private List<Float> yValues = new ArrayList<>();

    private ActivityBudgetBinding binding;

    private ExpenseRoom room;
    private BudgetDao budgetDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        room = Room.databaseBuilder(getApplicationContext(), ExpenseRoom.class, "expense_db")
                .build();

        budgetDao = room.budgetDao();

        super.onCreate(savedInstanceState);

        binding = ActivityBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.setBudgetBtn.setOnClickListener((btnV)->{
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.set_budget_btn_modal, null);
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();

            Button confirmBtn = sheetView.findViewById(R.id.confirm_button);
            TextInputEditText textInputEditText = sheetView.findViewById(R.id.setBudgetField);

            confirmBtn.setOnClickListener((v)->{
                if(textInputEditText.getText().toString().isEmpty()) {
                    bottomSheetDialog.dismiss();
                } else {
                    float amount = Float.parseFloat(textInputEditText.getText().toString());
                    new Thread(()->{
                        budgetDao.newRecord(
                                new BudgetTable(
                                        amount,
                                        amount
                                )
                        );
                        runOnUiThread(() -> {
                            bottomSheetDialog.dismiss();
                            setBudget();
                            setGraph();
                        });
                    }).start();
                }
            });
        });
        setGraph();
        setBudget();
    }

    private void setBudget() {
        new Thread(()->{
            BudgetTable lastBudget = budgetDao.getLastBudget();
            if (lastBudget != null) {
                runOnUiThread(() -> binding.availabeAmount.setText("Available: " + lastBudget.current_amount + "$"));
            }
        }).start();
    }

    private void setGraph() {
        LineChart chart = binding.lineChart;
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        chart.getAxisRight().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setLabelCount(6, false);

        new Thread(() -> {
            List<BudgetTable> budgets = budgetDao.getAll(); // blocking call assumed

            if (budgets.isEmpty()) {
                runOnUiThread(() -> {
                    chart.setData(null);
                    chart.invalidate();
                });
                return;
            }

            List<Entry> entries = new ArrayList<>();
            List<String> xLabels = new ArrayList<>();

            for (int i = 0; i < budgets.size(); i++) {
                BudgetTable b = budgets.get(i);
                entries.add(new Entry(i, (float) b.current_amount));
                xLabels.add("" + (i+1)); // ← improve this
            }

            runOnUiThread(() -> {
                LineDataSet dataSet = new LineDataSet(entries, "Budget");
                dataSet.setColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
                dataSet.setLineWidth(3.5f);          // ← reduced from 20f
                dataSet.setCircleRadius(4.5f);
                dataSet.setDrawCircleHole(false);
                dataSet.setDrawValues(false);
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                LineData lineData = new LineData(dataSet);

                xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
                xAxis.setLabelCount(xLabels.size(), true);

                chart.setData(lineData);
                chart.animateY(1200, Easing.EaseInOutCubic);
                chart.invalidate();
            });
        }).start();
    }
}
