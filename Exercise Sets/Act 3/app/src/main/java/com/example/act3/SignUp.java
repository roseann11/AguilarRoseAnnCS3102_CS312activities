package com.example.act3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.act3.R;
import com.example.act3.FirebaseHelper;
import com.example.act3.HelperClass;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    EditText birthdate, birthtime, email, username, password, phone, interests;
    AutoCompleteTextView country, state;
    Button submit, login;
    FirebaseHelper firebaseHelper;
    String[] stateArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize EditText, AutoCompleteTextView, and Button
        birthdate = findViewById(R.id.birthdate);
        birthtime = findViewById(R.id.birthtime);
        email = findViewById(R.id.eid);
        username = findViewById(R.id.unm);
        password = findViewById(R.id.pwd);
        phone = findViewById(R.id.pno);
        interests = findViewById(R.id.interset);
        submit = findViewById(R.id.regi);
        login = findViewById(R.id.login);
        country = findViewById(R.id.autoCompleteTextView);
        state = findViewById(R.id.autoCompleteTextView3);

        // Initialize FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Setup AutoCompleteTextView for countries
        String[] countries = getResources().getStringArray(R.array.array_region_dropdown);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);
        country.setAdapter(countryAdapter);

        // Setup listeners for country and state dropdowns
        state.setOnFocusChangeListener(this::onStateDropDownFocused);
        country.setOnItemClickListener(this::onCountryClicked);

        // Birthdate picker logic
        birthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this,
                    (view, year1, monthOfYear, dayOfMonth) -> birthdate.setText(String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1)), year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            datePickerDialog.show();
        });

        // Birth time picker logic
        birthtime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            boolean is24HourFormat = false;

            TimePickerDialog timePickerDialog = new TimePickerDialog(SignUp.this,
                    (view, hourOfDay, minute1) -> {
                        String period = (hourOfDay < 12) ? "AM" : "PM";
                        int hour12 = (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12;
                        birthtime.setText(String.format("%02d:%02d %s", hour12, minute1, period));
                    }, hour, minute, is24HourFormat);

            timePickerDialog.show();
        });

        // Submit button functionality
        submit.setOnClickListener(v -> {
            if (validateFields()) {
                String usernameText = username.getText().toString().trim();
                String emailText = email.getText().toString().trim();

                // Check if email already exists
                firebaseHelper.checkEmailExists(emailText, emailExists -> {
                    if (emailExists) {
                        email.setError("Email already exists, try another email.");
                        Toast.makeText(SignUp.this, "Email already exists, please try another email.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Check if username already exists
                        firebaseHelper.checkUsernameExists(usernameText, usernameExists -> {
                            if (usernameExists) {
                                username.setError("Username already exists.");
                                Toast.makeText(SignUp.this, "Username already exists, please choose another username.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Both email and username are unique, proceed with registration
                                HelperClass user = new HelperClass(
                                        usernameText,
                                        emailText,
                                        password.getText().toString().trim(),
                                        interests.getText().toString().trim(),
                                        Integer.parseInt(phone.getText().toString().trim()),
                                        parseDate(birthtime.getText().toString().trim()),
                                        parseDate(birthdate.getText().toString().trim())
                                );

                                // Save user data to Firebase
                                firebaseHelper.saveUser(user, new FirebaseHelper.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(SignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        // Navigate to the login screen after successful registration
                                        Intent intent = new Intent(SignUp.this, LogIn.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        Toast.makeText(SignUp.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        // Set onClickListener for Login button
        login.setOnClickListener(v -> {
            // Navigate to the LogIn activity
            Intent intent = new Intent(SignUp.this, LogIn.class);
            startActivity(intent);
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

        return valid;
    }

    // Method to validate email
    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void onStateDropDownFocused(View v, boolean hasFocus) {
        if (!hasFocus) return;
        if (stateArray != null) return;

        Toast.makeText(getApplicationContext(),
                "Select a region first.",
                Toast.LENGTH_SHORT).show();
    }

    private void onCountryClicked(AdapterView<?> adapterView, View v, int index, long l) {
        String item = adapterView.getItemAtPosition(index).toString();
        stateArray = getStateArray(item);

        if (stateArray.length > 0) {
            ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stateArray);
            state.setAdapter(statesAdapter);
            state.showDropDown();
        } else {
            state.setAdapter(null);
        }
    }

    private String[] getStateArray(String selectedCountry) {
        switch (selectedCountry) {
            case "Region I":
                return getResources().getStringArray(R.array.region_one_dropdown);
            case "Region II":
                return getResources().getStringArray(R.array.region_two_dropdown);
            case "Region III":
                return getResources().getStringArray(R.array.region_three_dropdown);
            case "Region IV-A":
                return getResources().getStringArray(R.array.region_four_a_dropdown);
            case "Region IV-B":
                return getResources().getStringArray(R.array.region_four_b_dropdown);
            case "Region V":
                return getResources().getStringArray(R.array.region_five_dropdown);
            case "Region VI":
                return getResources().getStringArray(R.array.region_six_dropdown);
            case "Region VII":
                return getResources().getStringArray(R.array.region_seven_dropdown);
            case "Region VIII":
                return getResources().getStringArray(R.array.region_eight_dropdown);
            case "Region IX":
                return getResources().getStringArray(R.array.region_nine_dropdown);
            case "Region X":
                return getResources().getStringArray(R.array.region_ten_dropdown);
            case "Region XI":
                return getResources().getStringArray(R.array.region_eleven_dropdown);
            case "Region XII":
                return getResources().getStringArray(R.array.region_twelve_dropdown);
            case "NCR":
                return getResources().getStringArray(R.array.region_ncr_dropdown);
            case "CAR":
                return getResources().getStringArray(R.array.region_car_dropdown);
            case "ARMM":
                return getResources().getStringArray(R.array.region_armm_dropdown);
            case "CARAGA":
                return getResources().getStringArray(R.array.region_caraga_dropdown);
            default:
                return new String[0];
        }
    }

    private long parseDate(String dateString) {
        // Convert dateString to a timestamp or any desired format
        return System.currentTimeMillis();
    }
}