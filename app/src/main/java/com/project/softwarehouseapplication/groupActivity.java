package com.project.softwarehouseapplication;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class groupActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private Uri imageUri;
    private StorageReference storageReference;
    private Button uploadImageButton, joinGroupButton;
    private ImageView profileImageView;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String userName, userEmail, imageUrl = "";  // Ensure imageUrl is initialized as an empty string
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        // Retrieve userName and userEmail from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        initUI();  // Initialize UI components
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        setListeners();  // Set up button listeners
    }
    private void initUI() {
        uploadImageButton = findViewById(R.id.btnUploadImage);
        joinGroupButton = findViewById(R.id.btnJoinGroup);
        profileImageView = findViewById(R.id.uploadedImageView);
    }
    private void setListeners() {
        uploadImageButton.setOnClickListener(v -> openFileChooser());
        // Listener for group join button
        joinGroupButton.setOnClickListener(v -> {
            if (!imageUrl.isEmpty()) {
                Log.d("GroupActivity", "Image uploaded successfully, navigating to groupMemberActivity");
                navigateToGroupMemberActivity(userName, userEmail, imageUrl);  // Only proceed if the image has been uploaded
            } else {
                Toast.makeText(groupActivity.this, "Please upload an image before joining the group", Toast.LENGTH_SHORT).show();
                Log.d("GroupActivity", "Image URL is empty, cannot join group");
            }
        });
    }
    private void openFileChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d("GroupActivity", "Image selected: " + imageUri.toString());
            uploadImageToFirebase();  // Upload selected image to Firebase
        } else {
            Log.d("GroupActivity", "Image selection failed");
        }
    }
    private void uploadImageToFirebase() {
        if (imageUri != null) {
            String fileExtension = getFileExtension(imageUri);
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + fileExtension);
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();  // Save the uploaded image URL
                    Log.d("ImageUpload", "Uploaded Image URL: " + imageUrl);
                    // Load the image into the ImageView using Glide
                    Glide.with(groupActivity.this)
                            .load(imageUrl)
                            .skipMemoryCache(true)
                            .into(profileImageView);
                    // Now update the image URL in Firebase
                    updateUserImageInFirebase(imageUrl);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(groupActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                Log.e("GroupActivity", "Image upload failed: " + e.getMessage());
            });
        } else {
            Log.e("GroupActivity", "No image URI selected");
        }
    }
    private void updateUserImageInFirebase(String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = database.child(userId);
            userRef.child("profileImageUrl").setValue(imageUrl).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("FirebaseDB", "User image URL updated successfully.");
                } else {
                    Log.e("FirebaseDB", "Failed to update image URL", task.getException());
                }
            });
        } else {
            Toast.makeText(groupActivity.this, "User is not logged in", Toast.LENGTH_SHORT).show();
            Log.e("GroupActivity", "No user is logged in");
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void navigateToGroupMemberActivity(String userName, String userEmail, String imageUrl) {
        Intent intent = new Intent(groupActivity.this, groupsMemberActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("profileImageUrl", imageUrl);  // Pass the image URL to the next activity
        startActivity(intent);
        finish();
    }
}

