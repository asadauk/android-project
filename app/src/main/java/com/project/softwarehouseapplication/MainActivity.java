package com.project.softwarehouseapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");
        EditText editTextName = findViewById(R.id.username);
        EditText editTextEmail = findViewById(R.id.userEmail);
        EditText editTextPassword = findViewById(R.id.userPassword);
        Button buttonRegister = findViewById(R.id.btnRegister);
        Button buttonLogin = findViewById(R.id.btnLogin);
        buttonRegister.setOnClickListener(view -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (password.length() < 6) {
                    Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, password);
                }
            }
        });
        buttonLogin.setOnClickListener(view -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }
    private void registerUser(String name, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    // Create a User object
                    User user = new User(name, email);
                    // Store user data quickly in Firebase Realtime Database
                    database.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            Log.d("FirebaseDB", "User data stored successfully.");
                            Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            navigateToGroupActivity(name, email);
                        } else {
                            Log.e("FirebaseDB", "Failed to store user data", dbTask.getException());
                            Toast.makeText(MainActivity.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e("FirebaseAuth", "Registration failed", task.getException());
                Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if (firebaseUser != null) {
                    database.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                navigateToGroupActivity(name, email);
                            } else {
                                Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e("FirebaseAuth", "Login failed", task.getException());
                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void navigateToGroupActivity(String name, String email) {
        Intent intent = new Intent(MainActivity.this, groupActivity.class);
        intent.putExtra("userName", name);
        intent.putExtra("userEmail", email);
        startActivity(intent);
        finish();
    }
}