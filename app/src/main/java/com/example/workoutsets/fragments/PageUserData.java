package com.example.workoutsets.fragments;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.workoutsets.ApiConnection.API_DELETE;
import com.example.workoutsets.ApiConnection.API_GET;
import com.example.workoutsets.R;
import com.example.workoutsets.entity.WorkoutSet;

import java.util.List;


public class PageUserData extends Fragment {

    private LinearLayout layout;
    // Get the screen size
    DisplayMetrics displayMetrics = new DisplayMetrics();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_user_data, container, false);

        // Find the LinearLayout in the inflated view
        layout = view.findViewById(R.id.myLinearLayout);

        // Fetch data from the API for the first time
        fetchDataFromApi();

        return view;
    }

    private void fetchDataFromApi() {
        // Fetch data from API in a new Thread
        new Thread(() -> {
            API_GET apiGet = new API_GET(getContext());
            List<WorkoutSet> sets = apiGet.getDataFromApi();

            // Update the UI on the main thread
            if (sets != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> updateUI(sets));
            }
        }).start();
    }

    private void updateUI(List<WorkoutSet> sets) {
        // Clear previous views before updating
        layout.removeAllViews();

        for (WorkoutSet set : sets) {
            // Create a horizontal LinearLayout for each row
            LinearLayout horizontalLayout = new LinearLayout(getContext());
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Create the TextView
            TextView viewWeight = blockData(set.getWeight()+"",100);
            TextView viewReps = blockData(set.getReps()+"",100);
            TextView viewMax = blockData(set.getMax(),100);
            TextView viewStage = blockData(set.getStage(),100);
            TextView viewDate = blockData(set.getDate(),100);

            // Create the Delete Button
            Button deleteButton = new Button(getContext());
            deleteButton.setText("X");
            deleteButton.setTextSize(20);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Set the button click listener to delete the item
            deleteButton.setOnClickListener(v -> {
                deleteWorkoutSet(set.getId());
            });

            // Add the TextView and Delete Button to the horizontal LinearLayout
            horizontalLayout.addView(viewWeight);
            horizontalLayout.addView(viewReps);
            horizontalLayout.addView(viewMax);
            horizontalLayout.addView(viewStage);
            horizontalLayout.addView(viewDate);
            horizontalLayout.addView(deleteButton);

            // Add the horizontal LinearLayout to the main layout
            layout.addView(horizontalLayout);
        }
    }

    private void deleteWorkoutSet(int id) {
        new Thread(() -> {
            API_DELETE apiDelete = new API_DELETE(getContext());
            boolean success = apiDelete.deleteDataFromApi(id); // Your delete method here

            // Update the UI on the main thread after deletion
            if (success && getActivity() != null) {
                getActivity().runOnUiThread(this::fetchDataFromApi); // Refresh data
            } else {
                // Handle failure (show a Toast or update UI accordingly)
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Failed to delete item.", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }


    private TextView blockData(String text,int size){


        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Define the percentage (e.g., 50% width and 30% height of the parent)
        int widthPercentage = 18;  // 50% of the width
        int heightPercentage = 13; // 30% of the height

        int layoutWidth = (screenWidth * widthPercentage) / 100;
        int layoutHeight = (screenHeight * heightPercentage) / 100;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutWidth, layoutHeight);

        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);

        textView.setLayoutParams(new LinearLayout.LayoutParams(params));

        return textView;

    }

}