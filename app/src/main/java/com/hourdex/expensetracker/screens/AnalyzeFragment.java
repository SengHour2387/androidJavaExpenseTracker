package com.hourdex.expensetracker.screens;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hourdex.expensetracker.MainActivity;
import com.hourdex.expensetracker.R;
import com.hourdex.expensetracker.controllers.BudgetController;
import com.hourdex.expensetracker.database.tables.BudgetTable;
import com.hourdex.expensetracker.database.tables.CategoryTable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalyzeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalyzeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private BudgetController budgetController;

    private MainActivity mainActivity;

    private LineChart lineChart;
    private PieChart pieChartOnCountPerCat;
    private PieChart pieChartOnIncomePerCat;
    private PieChart pieChartOnOutcomePerCat;


    public AnalyzeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalyzeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalyzeFragment newInstance(String param1, String param2) {
        AnalyzeFragment fragment = new AnalyzeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        budgetController = new BudgetController(mainActivity.getBudgetDao());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analyze, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        pieChartOnCountPerCat = view.findViewById(R.id.pieChart);
        pieChartOnIncomePerCat = view.findViewById(R.id.pieChart2);
        pieChartOnOutcomePerCat = view.findViewById(R.id.pieChart3);

        setGraph();
        setupPieChart(pieChartOnCountPerCat , "Transactions/\nCategory");
        setupPieChart(pieChartOnIncomePerCat, "Income/\nCategory");
        setupPieChart(pieChartOnOutcomePerCat, "Outcome/\nCategory");
        loadPieChartDataOnCountPerCat();
        loadPieChartDataOnIncomePerCat();
        loadPieChartDataOnOutcomePerCat();

        return view;
    }

    private void setGraph() {
        LineChart chart = lineChart;
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
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setLabelCount(6, false);

        new Thread(() -> {
            List<BudgetTable> budgets = budgetController.getAllBudget();

            if (budgets.isEmpty()) {
                mainActivity.runOnUiThread(() -> {
                    if (!isAdded()) return;
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
                xLabels.add("" + (i+1));
            }

            mainActivity.runOnUiThread(() -> {
                if (!isAdded()) return;
                LineDataSet dataSet = new LineDataSet(entries, "Budget");
                dataSet.setColor(ContextCompat.getColor(mainActivity, com.google.android.material.R.color.design_default_color_primary));
                dataSet.setLineWidth(5f);
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
    private void setupPieChart( PieChart pieChart , String holdText ) {
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(0f, 20f, 30f, 5f);


        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(255);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(85f);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(holdText);
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.setRotationAngle(0f);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);


        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(10f);

        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void loadPieChartDataOnCountPerCat() {
        new Thread(()->{
            ArrayList<PieEntry> entries = new ArrayList<>();
            List<CategoryTable> allCategory = mainActivity.getCategoryDao().getAll();

            allCategory.forEach( categoryTable -> {
                int count = mainActivity.getTransactionDao().getTransactionCountByCategory(categoryTable.id);
                if(count>0) {
                    entries.add(new PieEntry(count, categoryTable.name));
                }
            } );

            mainActivity.runOnUiThread(() -> {
                if (!isAdded()) return;
                PieDataSet dataSet = new PieDataSet(entries, "Categories");

                dataSet.setSliceSpace(5f);
                dataSet.setSelectionShift(8f);
                dataSet.setDrawIcons(false);

                // few colors
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#4CAF50"));
                colors.add(Color.parseColor("#2196F3"));
                colors.add(Color.parseColor("#FF9800"));
                colors.add(Color.parseColor("#9E9E9E"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#FF5722"));
                colors.add(Color.parseColor("#607D8B"));
                dataSet.setColors(colors);

                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter(pieChartOnCountPerCat));
                pieData.setValueTextSize(14f);
                pieData.setValueTextColor(Color.BLUE);
                pieData.setValueTypeface(Typeface.DEFAULT_BOLD);

                pieChartOnCountPerCat.setData(pieData);
                pieChartOnCountPerCat.highlightValues(null);
                dataSet.setYValuePosition( PieDataSet.ValuePosition.OUTSIDE_SLICE );
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {

                        if(value == 1) {
                            return "A tran";
                        } else  {
                            return String.format("%.0f trans", value);
                        }
                    }
                });
                pieChartOnCountPerCat.invalidate();
            });
        }).start();
    }

    private void loadPieChartDataOnOutcomePerCat() {
        new Thread(()->{
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            List<CategoryTable> allCategory = mainActivity.getCategoryDao().getAll();

            allCategory.forEach( categoryTable -> {
                Double amount = mainActivity.getTransactionDao().getTransactionOutcomeByCategory(categoryTable.id );

                if (amount != null) {
                    pieEntries.add( new PieEntry(amount.floatValue() * -1,categoryTable.name) );
                }
            } );

            mainActivity.runOnUiThread(() -> {
                if (!isAdded()) return;
                PieDataSet dataSet = new PieDataSet(pieEntries, "Categories");

                dataSet.setSliceSpace(5f);
                dataSet.setSelectionShift(8f);
                dataSet.setDrawIcons(false);

                // few colors
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#4CAF50"));
                colors.add(Color.parseColor("#2196F3"));
                colors.add(Color.parseColor("#FF9800"));
                colors.add(Color.parseColor("#9E9E9E"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#FF5722"));
                colors.add(Color.parseColor("#607D8B"));
                dataSet.setColors(colors);

                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter(pieChartOnOutcomePerCat));
                pieData.setValueTextSize(14f);
                pieData.setValueTextColor(Color.RED);
                pieData.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format("-%.2f$",value);
                    }
                });
                pieData.setValueTypeface(Typeface.DEFAULT_BOLD);

                pieChartOnOutcomePerCat.setData(pieData);
                pieChartOnOutcomePerCat.highlightValues(null);
                dataSet.setYValuePosition( PieDataSet.ValuePosition.OUTSIDE_SLICE );
                pieChartOnOutcomePerCat.invalidate();
            });
        }).start();
    }
    private void loadPieChartDataOnIncomePerCat() {
        new Thread(()->{
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            List<CategoryTable> allCategory = mainActivity.getCategoryDao().getAll();

            allCategory.forEach( categoryTable -> {
                Double amount = mainActivity.getTransactionDao().getTransactionIncomeByCategory(categoryTable.id );

                if (amount != null) {
                    pieEntries.add( new PieEntry(amount.floatValue(),categoryTable.name) );
                }
            } );

            mainActivity.runOnUiThread(() -> {
                if (!isAdded()) return;
                PieDataSet dataSet = new PieDataSet(pieEntries, "Categories");

                dataSet.setSliceSpace(5f);
                dataSet.setSelectionShift(8f);
                dataSet.setDrawIcons(false);

                // few colors
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#4CAF50"));
                colors.add(Color.parseColor("#2196F3"));
                colors.add(Color.parseColor("#FF9800"));
                colors.add(Color.parseColor("#9E9E9E"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#3F51B5"));
                colors.add(Color.parseColor("#F44336"));
                colors.add(Color.parseColor("#FF5722"));
                colors.add(Color.parseColor("#607D8B"));
                dataSet.setColors(colors);

                PieData pieData = new PieData(dataSet);
                pieData.setValueFormatter(new PercentFormatter(pieChartOnIncomePerCat));
                pieData.setValueTextSize(14f);
                pieData.setValueTextColor(0xff00BB00);
                pieData.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.format("+%.2f$",value);
                    }
                });
                pieData.setValueTypeface(Typeface.DEFAULT_BOLD);

                pieChartOnIncomePerCat.setData(pieData);
                dataSet.setYValuePosition( PieDataSet.ValuePosition.OUTSIDE_SLICE );
                pieChartOnIncomePerCat.invalidate();
            });
        }).start();
    }
}