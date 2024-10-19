package com.project.softwarehouseapplication;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
public class ProfileInputActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private EditText nameEditText;
    private Uri imageUri;
    private DatabaseReference database;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_input);
        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");
        // Initialize UI components
        profileImageView = findViewById(R.id.profile_image_view);
        nameEditText = findViewById(R.id.edit_text_name);
        Button uploadButton = findViewById(R.id.button_upload);
        // Set listener to pick an image
        profileImageView.setOnClickListener(v -> openFileChooser());
        // Set listener to save the profile data
        uploadButton.setOnClickListener(v -> uploadProfile());
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);  // Display the image in the ImageView
        }
    }
    private void uploadProfile() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Compress image to 50%
                byte[] data = baos.toByteArray();

                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
                UploadTask uploadTask = fileReference.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profileImageUrl = uri.toString();
                        String userId = database.push().getKey();
                        Member member = new Member(name, userId, profileImageUrl);
                        if (userId != null) {
                            database.child(userId).setValue(member).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileInputActivity.this, "Profile saved", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ProfileInputActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }).addOnFailureListener(e -> Toast.makeText(ProfileInputActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}