package com.example.act3;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private CheckBox showPasswordCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);

        // Handle window insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.pwd);
        loginButton = findViewById(R.id.login_btn);
        showPasswordCheckbox = findViewById(R.id.show_password_checkbox); // Ensure this ID matches XML

        // Set click listener for the login button
        loginButton.setOnClickListener(v -> loginUser());

        // Set listener for the "Show Password" checkbox
        showPasswordCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Move cursor to the end of the text after changing input type
            passwordField.setSelection(passwordField.getText().length());
        });

        // Find the redirect text view
        TextView redirectTextView = findViewById(R.id.loginRedirectText);

        // Set click listener for the redirect text view
        redirectTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, SignUp.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validate email and password
        if (!isEmailValid(email)) {
            emailField.setError("Invalid email address");
            return;
        }
        if (password.isEmpty()) {
            passwordField.setError("Password cannot be empty");
            return;
        }

        // Check user credentials
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        HelperClass user = userSnapshot.getValue(HelperClass.class);
                        if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
                            Log.d("LogIn", "Login successful for user: " + user.getUsername());
                            Intent intent = new Intent(LogIn.this, NewPage.class);
                            startActivity(intent);
                            finish(); // Optional: close login activity
                            return;
                        } else {
                            Log.d("LogIn", "Password does not match");
                        }
                    }
                    // Password did not match
                    Toast.makeText(LogIn.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    // Email not found
                    Toast.makeText(LogIn.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LogIn.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
