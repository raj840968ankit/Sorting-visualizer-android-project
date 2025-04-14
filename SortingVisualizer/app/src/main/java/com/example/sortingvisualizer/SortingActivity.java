package com.example.sortingvisualizer;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;

public class SortingActivity extends AppCompatActivity {

    private LinearLayout sortingContainer;
    private EditText etNumbers;
    private Button btnSort;
    private SeekBar speedControl;
    private Toolbar toolbar;
    private String selectedSortType;
    private int sortingSpeed = 800; // Default sorting speed in milliseconds

    private ArrayList<Integer> numbers = new ArrayList<>();
    // Each bar is represented by a RelativeLayout that holds three views:
    // [0] Top label (value), [1] Bar view, [2] Bottom label (index)
    private ArrayList<RelativeLayout> barLayouts = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set toolbar title to the selected sorting algorithm (e.g., "Bubble Sort")
        selectedSortType = getIntent().getStringExtra("SORT_TYPE");
        getSupportActionBar().setTitle(selectedSortType);

        sortingContainer = findViewById(R.id.sortingContainer);
        etNumbers = findViewById(R.id.etNumbers);
        btnSort = findViewById(R.id.btnSort);
        speedControl = findViewById(R.id.speedControl);

        btnSort.setOnClickListener(view -> startSorting());

        speedControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sortingSpeed = 1200 - (progress * 10);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar){}
            @Override public void onStopTrackingTouch(SeekBar seekBar){}
        });
    }

    private void startSorting(){
        String inputText = etNumbers.getText().toString().trim();
        if(inputText.isEmpty()){
            Toast.makeText(this, "Enter numbers (max 8) between 1-100.", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] inputArray = inputText.split("\\s+");
        if(inputArray.length > 8){
            Toast.makeText(this, "Maximum 8 numbers allowed!", Toast.LENGTH_SHORT).show();
            return;
        }
        numbers.clear();
        for(String s : inputArray){
            try{
                int value = Integer.parseInt(s);
                if(value < 1 || value > 100){
                    Toast.makeText(this, "Values must be between 1 and 100!", Toast.LENGTH_SHORT).show();
                    return;
                }
                numbers.add(value);
            } catch(NumberFormatException e){
                Toast.makeText(this, "Invalid input! Enter numbers only.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        drawBars();
        new Thread(() -> {
            try {
                switch (selectedSortType) {
                    case "Bubble Sort":
                        bubbleSort();
                        break;
                    case "Selection Sort":
                        selectionSort();
                        break;
                    case "Insertion Sort":
                        insertionSort();
                        break;
                    case "Shell Sort":
                        shellSort();
                        break;
                    default:
                        runOnUiThread(() -> Toast.makeText(SortingActivity.this, "Sorting algorithm not implemented!", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SortingActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();


    }

    // Create a RelativeLayout for a single bar with top value and bottom index
    private RelativeLayout createBarLayout(int index, int value) {
        // Create a container for the bar, which will hold three views:
        // 1. Top label: Displays the current value.
        // 2. Middle view: The bar itself (height based on 'value', unsorted color gray).
        // 3. Bottom label: Displays the fixed index.
        RelativeLayout barLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(8, 0, 8, 0);
        barLayout.setLayoutParams(lp);

        // Top label: Displays the value of the bar.
        TextView tvValue = new TextView(this);
        tvValue.setId(View.generateViewId());
        RelativeLayout.LayoutParams lpValue = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpValue.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lpValue.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvValue.setLayoutParams(lpValue);
        tvValue.setText(String.valueOf(value));
        tvValue.setTextColor(Color.BLACK);

        // Middle view: the bar itself; height is proportional to 'value'
        TextView tvBar = new TextView(this);
        tvBar.setId(View.generateViewId());
        RelativeLayout.LayoutParams lpBar = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                value * 8);
        lpBar.addRule(RelativeLayout.BELOW, tvValue.getId());
        tvBar.setLayoutParams(lpBar);
        tvBar.setBackgroundResource(R.drawable.rounded_bar);
        tvBar.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        // Make the bar clickable so that pressed state is applied
        tvBar.setClickable(true);
        tvBar.setFocusable(true);

        // Bottom label: Displays the index (fixed) below the bar.
        TextView tvIndex = new TextView(this);
        RelativeLayout.LayoutParams lpIndex = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpIndex.addRule(RelativeLayout.BELOW, tvBar.getId());
        lpIndex.addRule(RelativeLayout.CENTER_HORIZONTAL);
        tvIndex.setLayoutParams(lpIndex);
        tvIndex.setText("[" + index + "]");
        tvIndex.setTextColor(Color.BLACK);

        // Add all views to the container.
        // index 0: tvValue, index 1: tvBar, index 2: tvIndex.
        barLayout.addView(tvValue);
        barLayout.addView(tvBar);
        barLayout.addView(tvIndex);

        return barLayout;
    }

    private void drawBars() {
        runOnUiThread(() -> {
            sortingContainer.removeAllViews();
            barLayouts.clear();
            for (int i = 0; i < numbers.size(); i++) {
                RelativeLayout barLayout = createBarLayout(i, numbers.get(i));
                sortingContainer.addView(barLayout);
                barLayouts.add(barLayout);
            }
            // Ensure layout is complete before starting animations.
            sortingContainer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                // Now the container is laid out; you can log the child count.
                int childCount = sortingContainer.getChildCount();
                android.util.Log.d("DEBUG", "After layout, sortingContainer child count: " + childCount);
                // Optionally, start your sorting algorithm here if needed.
            });
        });
    }

    // Modified highlightBars: use the boolean flag to set the pressed state and refresh the drawable.
    private void highlightBars(int i, int j, boolean highlight) {
        runOnUiThread(() -> {
            TextView tvBar1 = (TextView) barLayouts.get(i).getChildAt(1);
            TextView tvBar2 = (TextView) barLayouts.get(j).getChildAt(1);
            tvBar1.setPressed(highlight);
            tvBar2.setPressed(highlight);
            tvBar1.refreshDrawableState();
            tvBar2.refreshDrawableState();
        });
    }

    // Swap the bars at indices i and j, animate the change, reorder views, and update display
    private void swapBars(final int i, final int j) {
        // 1. Highlight bars i and j (yellow) to indicate comparison
        runOnUiThread(() ->  highlightBars(i, j, true));
        try {
            Thread.sleep(sortingSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. Swap the underlying data in 'numbers' & 'barLayouts'
        Collections.swap(numbers, i, j);
        Collections.swap(barLayouts, i, j);

        // 3. Reorder the container's children to match the new order (no animation)
        runOnUiThread(() -> {
            sortingContainer.removeAllViews();
            for (RelativeLayout barLayout : barLayouts) {
                sortingContainer.addView(barLayout);
            }
        });

        // 4. Update the two bars' displays after the swap
        updateBar(i);
        updateBar(j);

        // 5. Un-highlight the bars (reset color to default gray via the selector)
        runOnUiThread(() -> highlightBars(i, j, false));
        try {
            Thread.sleep(sortingSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    // Update a single bar layout at a given index to reflect the current number value
    private void updateBar(final int index) {
        runOnUiThread(() -> {
            // Log the current size and check index bounds.
            int layoutSize = barLayouts.size();
            android.util.Log.d("DEBUG", "updateBar: index = " + index + ", barLayouts.size() = " + layoutSize);
            if(index < 0 || index >= layoutSize) {
                android.util.Log.e("ERROR", "updateBar: index out of bounds!");
                return;
            }
            RelativeLayout barLayout = barLayouts.get(index);
            if (barLayout.getChildCount() < 2) {
                android.util.Log.e("ERROR", "updateBar: insufficient children in barLayout at index " + index);
                return;
            }
            TextView tvValue = (TextView) barLayout.getChildAt(0);
            TextView tvBar = (TextView) barLayout.getChildAt(1);
            int val = numbers.get(index);
            tvValue.setText(String.valueOf(val));
            ViewGroup.LayoutParams params = tvBar.getLayoutParams();
            params.height = val * 8;
            tvBar.setLayoutParams(params);
            // Reapply the background resource to reset the state (unsorted/gray)
            tvBar.setBackgroundResource(R.drawable.rounded_bar);
        });
        try {
            Thread.sleep(sortingSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // Check if the array is sorted
    private boolean isSorted(){
        for(int i = 0; i < numbers.size()-1; i++){
            if(numbers.get(i) > numbers.get(i+1)) return false;
        }
        return true;
    }

    // Bubble Sort
    private void bubbleSort(){
        int n = numbers.size();
        for(int i = 0; i < n - 1; i++){
            for(int j = 0; j < n - i - 1; j++){
                // Highlight the bars for this comparison (set pressed state = true, which shows yellow)
                int finalJ = j;
                runOnUiThread(() -> highlightBars(finalJ, finalJ+1, true));
                try {
                    Thread.sleep(sortingSpeed / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Check if a swap is needed
                if (numbers.get(j) > numbers.get(j+1)){
                    // If swap is required, let swapBars handle highlighting and reordering.
                    swapBars(j, j+1);
                } else {
                    // If no swap, un-highlight the bars (set pressed = false)
                    runOnUiThread(() -> highlightBars(finalJ, finalJ+1, false));
                    try {
                        Thread.sleep(sortingSpeed / 2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            // Mark the element at index (n - i - 1) as sorted (turn bar green)
            int finalI = i;
            runOnUiThread(() -> {
                TextView tvBar = (TextView) barLayouts.get(n - finalI - 1).getChildAt(1);
                tvBar.setSelected(true);
            });
        }
        // Mark the first element as sorted after all iterations.
        runOnUiThread(() -> {
            TextView tvBar = (TextView) barLayouts.get(0).getChildAt(1);
            tvBar.setSelected(true);
        });
    }


    private void selectionSort() {
        int n = numbers.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                final int currentJ = j;
                final int start = i;
                // Highlight the comparison between the current candidate and the candidate at j.
                int finalMinIdx = minIdx;
                runOnUiThread(() -> highlightBars(start, currentJ, true));
                try {
                    Thread.sleep(sortingSpeed / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Update candidate if a smaller element is found.
                if (numbers.get(j) < numbers.get(minIdx)) {
                    minIdx = j;
                }
                // Un-highlight after the comparison.
                runOnUiThread(() -> highlightBars(start, currentJ, false));
                try {
                    Thread.sleep(sortingSpeed / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            swapBars(i, minIdx);
            // Mark the element at index i as sorted (green)
            final int sortedIndex = i;
            runOnUiThread(() -> {
                TextView tvBar = (TextView) barLayouts.get(sortedIndex).getChildAt(1);
                tvBar.setSelected(true);
            });
        }
        // Mark the last element as sorted
        runOnUiThread(() -> {
            TextView tvBar = (TextView) barLayouts.get(n - 1).getChildAt(1);
            tvBar.setSelected(true);
        });
    }

    private void insertionSort() {
        int n = numbers.size();

        // Mark bar 0 as sorted (green) initially.
        runOnUiThread(() -> {
            TextView tvBar = (TextView) barLayouts.get(0).getChildAt(1);
            tvBar.setSelected(true);
            tvBar.refreshDrawableState();
        });

        for (int i = 1; i < n; i++) {
            // Ensure left portion [0..i-1] remains green.
            for (int k = 0; k < i; k++) {
                final int idx = k;
                runOnUiThread(() -> {
                    TextView tvBar = (TextView) barLayouts.get(idx).getChildAt(1);
                    tvBar.setSelected(true);
                    tvBar.setPressed(false);
                    tvBar.refreshDrawableState();
                });
            }

            // Force comparisons for each j from i down to 1.
            for (int j = i; j > 0; j--) {
                final int idx1 = j - 1;
                final int idx2 = j;

                // Highlight the two bars (pressed state = yellow)
                runOnUiThread(() -> {
                    TextView bar1 = (TextView) barLayouts.get(idx1).getChildAt(1);
                    TextView bar2 = (TextView) barLayouts.get(idx2).getChildAt(1);
                    // Clear any sorted (green) state so yellow can show
                    bar1.setSelected(false);
                    bar2.setSelected(false);
                    bar1.setPressed(true);
                    bar2.setPressed(true);
                    bar1.refreshDrawableState();
                    bar2.refreshDrawableState();
                });
                try {
                    Thread.sleep(sortingSpeed / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Check if swap is needed.
                if (numbers.get(j) < numbers.get(j - 1)) {
                    Collections.swap(numbers, j, j - 1);
                    Collections.swap(barLayouts, j, j - 1);
                    runOnUiThread(() -> {
                        sortingContainer.removeAllViews();
                        for (RelativeLayout rl : barLayouts) {
                            sortingContainer.addView(rl);
                        }
                    });
                }
                // Un-highlight the bars, reverting them to green (sorted state).
                runOnUiThread(() -> {
                    TextView bar1 = (TextView) barLayouts.get(idx1).getChildAt(1);
                    TextView bar2 = (TextView) barLayouts.get(idx2).getChildAt(1);
                    bar1.setPressed(false);
                    bar2.setPressed(false);
                    bar1.setSelected(true);
                    bar2.setSelected(true);
                    bar1.refreshDrawableState();
                    bar2.refreshDrawableState();
                });
                try {
                    Thread.sleep(sortingSpeed / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // After processing element i, ensure left portion [0..i] is marked green.
            for (int k = 0; k <= i; k++) {
                final int idx = k;
                runOnUiThread(() -> {
                    TextView tvBar = (TextView) barLayouts.get(idx).getChildAt(1);
                    tvBar.setSelected(true);
                    tvBar.setPressed(false);
                    tvBar.refreshDrawableState();
                });
            }
        }
        // Final pass: ensure entire array [0..n-1] is marked green.
        for (int i = 0; i < n; i++) {
            final int idx = i;
            runOnUiThread(() -> {
                TextView tvBar = (TextView) barLayouts.get(idx).getChildAt(1);
                tvBar.setSelected(true);
                tvBar.setPressed(false);
                tvBar.refreshDrawableState();
            });
        }
    }

    private void shellSort() {
        int n = numbers.size();

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = numbers.get(i);
                int j = i;

                while (j >= gap) {
                    final int idx1 = j;
                    final int idx2 = j - gap;

                    // Highlight comparison
                    runOnUiThread(() -> highlightBars(idx1, idx2, true));
                    sleep();

                    if (numbers.get(j - gap) > temp) {
                        // Swap bars visually and in data
                        swapBars(j, j - gap);
                        j -= gap;
                    } else {
                        // Still unhighlight even if no swap
                        runOnUiThread(() -> highlightBars(idx1, idx2, false));
                        sleep();
                        break;
                    }

                    // Unhighlight after swapping comparison
                    runOnUiThread(() -> highlightBars(idx1, idx2, false));
                    sleep();
                }

                // Place temp in correct position (if needed)
                numbers.set(j, temp);
                final int finalJ = j;
                runOnUiThread(() -> {
                    TextView tvBar = (TextView) barLayouts.get(finalJ).getChildAt(1);
                });
            }
        }

        // Final green highlight for all sorted bars
        for (int i = 0; i < n; i++) {
            final int idx = i;
            runOnUiThread(() -> {
                TextView tvBar = (TextView) barLayouts.get(idx).getChildAt(1);
                tvBar.setSelected(true); // Assumes 'selected' = green
            });
        }
    }

    // Sleep helper to avoid repeating boilerplate
    private void sleep() {
        try {
            Thread.sleep(sortingSpeed / 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }









    // The updateBar() method updates the bar's height and value.
    private void updateBar(final int index, int newValue) {
        runOnUiThread(() -> {
            RelativeLayout barLayout = barLayouts.get(index);
            TextView tvValue = (TextView) barLayout.getChildAt(0);
            TextView tvBar = (TextView) barLayout.getChildAt(1);
            tvValue.setText(String.valueOf(newValue));
            ViewGroup.LayoutParams params = tvBar.getLayoutParams();
            params.height = newValue * 8;
            tvBar.setLayoutParams(params);
            // Reset state so that the selector (rounded_bar.xml) applies the default gray\n        tvBar.setSelected(false);
        });
        try {
            Thread.sleep(sortingSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
