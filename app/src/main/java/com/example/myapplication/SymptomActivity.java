package com.example.myapplication;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Button;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.google.android.material.slider.Slider;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomActivity extends AppCompatActivity {

    String timestamp = null;

    Button button_ListToDB, button_SymptomToList;

    DBHandler DBHandler; // Database handler instance

    Slider m_slider;
    float m_rating = 0;

    AutoCompleteTextView m_AutoCompleteTextView; // Autocomplete text view for selecting symptoms
    ArrayAdapter<String> m_Adapters; // Adapter for managing symptoms input list
    String m_curSelection = null;

    Map<String, Float> m_sympts = new Hashtable<>();
    TextView m_curSelectionsSoFar;

    String[] input_lists = { // List of symptoms to choose from
            "nausea",
            "headache",
            "diarrhea",
            "soar throat",
            "fever",
            "muscle ache",
            "loss of smell or taste",
            "cough",
            "shortness of breath",
            "feeling tired"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms);

        DBHandler = new DBHandler(SymptomActivity.this); // Initialize DB handler

        // Initialize
        startSliderRank();
        startDropSymps();
        startPushToDB();
        startAddToList();
    }

    // Function to get current symptom selection
    private String getCurSelectFunc() {
        if (TextUtils.isEmpty(this.m_curSelection)) {
            return null;
        }

        return this.m_curSelection;
    }

    // Function to get the current rating value from slider
    private float getRateFunc() {
        return this.m_rating;
    }

    // Function to set the current rating value
    private void setRateFunc(float val) {
        this.m_rating = val;
    }

    // Initialize and set up the slider for symptom severity rating
    private void startSliderRank() {
        m_slider = findViewById(R.id.discrete_slider);

        m_slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider m_slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider m_slider) {
                setRateFunc(m_slider.getValue());
            }
        });
    }

    // Set the current symptom selected by the user
    private void setCurSelectFunc(String m_curSelection) {
        this.m_curSelection = m_curSelection;
    }

    // Initialize and set up the AutoCompleteTextView for selecting symptoms
    private void startDropSymps() {
        m_Adapters = new ArrayAdapter<>(this, R.layout.symptoms, input_lists);
        m_AutoCompleteTextView = findViewById(R.id.auto_complete_textview);
        m_AutoCompleteTextView.setAdapter(m_Adapters);

        m_AutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String temp = adapterView.getItemAtPosition(position).toString();
                setCurSelectFunc(temp);
            }
        });
    }

    // Initialize the button that pushes symptoms list to the database
    private void startPushToDB() {
        button_ListToDB = findViewById(R.id.PushDataToDB);

        button_ListToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> item = symptomListconnectDB();

                DBHandler.setDBMap(item);
                DBHandler.addDBItems();

                Toast.makeText(SymptomActivity.this, "save to sqlite success",
                        Toast.LENGTH_SHORT).show();

                cleanScreenFunc(); // Clear the UI after saving
            }
        });
    }

    // Prepare the symptom list and its ratings to be saved in the database
    private Map<String, String> symptomListconnectDB() {
        Map<String, String> item = new HashMap<>();
        for (String list : input_lists) {
            item.put(list, String.valueOf(0.0f));
        }

        for (String key : m_sympts.keySet()) {
            item.put(key, String.valueOf(m_sympts.get(key))); // Update with actual ratings
        }

        return item;
    }

    // Helper function to format the selected symptoms and their ratings for display
    private String searchFromMap() {
        StringBuilder temp = new StringBuilder();

        for (String key : m_sympts.keySet()) {
            temp.append(key + ": " + m_sympts.get(key) + "\n");
        }
        return temp.toString();
    }

    // Display selected symptoms and ratings on the screen
    private void setSelectedTextView() {
        m_curSelectionsSoFar = findViewById(R.id.dataSelectedSoFar);
        m_curSelectionsSoFar.setText(searchFromMap());
    }

    // Reset the UI elements after saving or adding symptoms
    private void cleanScreenFunc() {
        setCurSelectFunc(null);
        m_sympts.clear();

        setSelectedTextView(); // Update UI with cleared list
        m_slider.setValue(0.0f); // Reset slider to default value
    }

    // Initialize the button to add the current symptom and rating to the list
    private void startAddToList() {
        button_SymptomToList = findViewById(R.id.addToSymptoms);

        button_SymptomToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurSelectFunc() != null && getRateFunc() > 0) {
                    m_sympts.put(getCurSelectFunc(), getRateFunc());
                    setSelectedTextView(); // Update UI with selected symptoms
                } else {
                    Toast.makeText(SymptomActivity.this, "ensure selections already made",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
