package com.example.sortingvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private String[] sortingAlgorithms = {
            "Bubble Sort", "Selection Sort", "Insertion Sort", "Shell Sort"
    };

    private String username; // ✅ Declare here but initialize later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);

        // ✅ Now safe to extract from Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            username = intent.getStringExtra("username");
        } else {
            username = "Guest"; // Default value
        }

        ListView sortingListView = findViewById(R.id.sortingListView);
        SortingAdapter adapter = new SortingAdapter();
        sortingListView.setAdapter(adapter);

        TextView profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity3.this, "Profile Icon Clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity3.this, UserActionActivity.class);
                intent.putExtra("username", username); // ✅ Pass safely
                startActivity(intent);
            }
        });

        sortingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSort = sortingAlgorithms[position];

                AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
                animation.setDuration(300);
                view.startAnimation(animation);

                Toast.makeText(MainActivity3.this, selectedSort + " Selected", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity3.this, SortingActivity.class);
                intent.putExtra("SORT_TYPE", selectedSort);
                startActivity(intent);
            }
        });
    }

    private class SortingAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sortingAlgorithms.length;
        }

        @Override
        public Object getItem(int position) {
            return sortingAlgorithms[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity3.this).inflate(R.layout.list_item, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.tvSortName);
            textView.setText(sortingAlgorithms[position]);
            return convertView;
        }
    }
}
