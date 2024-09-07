package com.example.activity2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText birthdate, birthtime, email, username, password, phone, interests;
    AutoCompleteTextView country, state;
    Button submit;

    String[] countries = {"India", "Indonesia", "Africa", "Afghanistan"};
    String[] statesArray = {"Gujarat", "Goa", "Maharashtra", "Rajasthan", "Assam", "Bihar", "West Bengal"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize EditText, AutoCompleteTextView, and Button
        birthdate = findViewById(R.id.birthdate);
        birthtime = findViewById(R.id.birthtime);
        email = findViewById(R.id.eid);
        username = findViewById(R.id.unm);
        password = findViewById(R.id.pwd);
        phone = findViewById(R.id.pno);
        interests = findViewById(R.id.interset);
        submit = findViewById(R.id.regi);
        country = findViewById(R.id.autoCompleteTextView);
        state = findViewById(R.id.autoCompleteTextView3);

        // Setup AutoCompleteTextView for countries
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        country.setAdapter(countryAdapter);

        // Setup AutoCompleteTextView for states
        ArrayAdapter<String> statesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, statesArray);
        state.setAdapter(statesAdapter);

        // Birthdate picker logic
        birthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> birthdate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1)), year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Birth time picker logic
        birthtime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            boolean is24HourFormat = false;

            TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String period = (hourOfDay < 12) ? "AM" : "PM";
                        int hour12 = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12;
                        birthtime.setText(String.format("%02d:%02d %s", hour12, minute1, period));
                    }, hour, minute, is24HourFormat);

            timePickerDialog.show();
        });

        // Submit button functionality
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate all fields
                if (validateFields()) {
                    // Collect all the input data
                    String userData = "Username: " + username.getText().toString() + "\n" +
                            "Email: " + email.getText().toString() + "\n" +
                            "Phone: " + phone.getText().toString() + "\n" +
                            "Country: " + country.getText().toString() + "\n" +
                            "State: " + state.getText().toString() + "\n" +
                            "Interests: " + interests.getText().toString() + "\n" +
                            "Birthdate: " + birthdate.getText().toString() + "\n" +
                            "Birth Time: " + birthtime.getText().toString();

                    // Create an alert dialog to display the information
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Submitted Information")
                            .setMessage(userData)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            })
                            .show();
                }
            }
        });
    }

    // Method to validate all fields
    private boolean validateFields() {
        boolean valid = true;

        // Validate username
        if (username.getText().toString().trim().isEmpty()) {
            username.setError("Username cannot be empty");
            valid = false;
        }

        // Validate password
        if (password.getText().toString().trim().isEmpty()) {
            password.setError("Password cannot be empty");
            valid = false;
        }

        // Validate email
        if (!isEmailValid(email.getText().toString())) {
            email.setError("Invalid email address");
            valid = false;
        }

        // Validate phone number (must be 10 digits)
        if (phone.getText().toString().trim().length() != 10) {
            phone.setError("Phone number must be 10 digits");
            valid = false;
        }

        // Validate country selection
        if (country.getText().toString().trim().isEmpty()) {
            country.setError("Please select a country");
            valid = false;
        }

        // Validate state selection
        if (state.getText().toString().trim().isEmpty()) {
            state.setError("Please select a state");
            valid = false;
        }

        // Validate interests
        if (interests.getText().toString().trim().isEmpty()) {
            interests.setError("Interests cannot be empty");
            valid = false;
        }

        // Validate birthdate
        if (birthdate.getText().toString().trim().isEmpty()) {
            birthdate.setError("Please select your birthdate");
            valid = false;
        }

        // Validate birthtime
        if (birthtime.getText().toString().trim().isEmpty()) {
            birthtime.setError("Please select your birth time");
            valid = false;
        }

        return valid;
    }

    // Method to validate email
    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
