package com.example.act3;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FirebaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    public void checkUsernameExists(String username, UsernameCheckCallback callback) {
        Query query = databaseReference.orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                Log.d("FirebaseHelper", "Username check - exists: " + exists);
                callback.onComplete(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseHelper", "Error checking username: " + databaseError.getMessage());
                callback.onComplete(false); // Error checking username
            }
        });
    }

    public void checkEmailExists(String email, EmailCheckCallback callback) {
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                Log.d("FirebaseHelper", "Email check - exists: " + exists);
                callback.onComplete(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseHelper", "Error checking email: " + databaseError.getMessage());
                callback.onComplete(false); // Error checking email
            }
        });
    }

    public void saveUser(HelperClass user, Callback callback) {
        String userId = databaseReference.push().getKey();
        if (userId != null) {
            databaseReference.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseHelper", "Error saving user: " + e.getMessage());
                        callback.onFailure(e.getMessage());
                    });
        } else {
            callback.onFailure("Failed to generate user ID");
        }
    }

    public interface Callback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface UsernameCheckCallback {
        void onComplete(boolean exists);
    }

    public interface EmailCheckCallback {
        void onComplete(boolean exists);
    }
}
