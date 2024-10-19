package com.project.softwarehouseapplication;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
public class groupsMemberActivity extends AppCompatActivity {
    private DatabaseReference database;
    private RecyclerView recyclerViewMembers;
    private groupMemberAdapter memberAdapter;
    private List<Member> membersList = new ArrayList<>();
    private ImageView callIcon;  // New ImageView for the call icon

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_member);

        // Retrieve the passed data from the Intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String userEmail = intent.getStringExtra("userEmail");

        // Log the data to ensure it's passed correctly
        Log.d("groupsMemberActivity", "User Name: " + userName);
        Log.d("groupsMemberActivity", "User Email: " + userEmail);

        // Initialize RecyclerView and adapter
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new groupMemberAdapter(this, membersList);
        recyclerViewMembers.setAdapter(memberAdapter);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference();

        // Load all users from Firebase
        loadUsers();

        // Handle the call icon click
        callIcon = findViewById(R.id.callIcon);
        callIcon.setOnClickListener(v -> startVideoCall());
    }

    private void loadUsers() {
        DatabaseReference usersRef = database.child("users");
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Member member = dataSnapshot.getValue(Member.class);
                if (member != null) {
                    membersList.add(member);  // Add member to the list
                    memberAdapter.notifyDataSetChanged();  // Notify adapter of changes
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Member updatedMember = dataSnapshot.getValue(Member.class);
                if (updatedMember != null) {
                    for (int i = 0; i < membersList.size(); i++) {
                        if (membersList.get(i).getId().equals(updatedMember.getId())) {
                            membersList.set(i, updatedMember);  // Update member in the list
                            memberAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Member removedMember = dataSnapshot.getValue(Member.class);
                if (removedMember != null) {
                    for (int i = 0; i < membersList.size(); i++) {
                        if (membersList.get(i).getId().equals(removedMember.getId())) {
                            membersList.remove(i);
                            memberAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle when a child is moved, if needed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(groupsMemberActivity.this, "Failed to load members.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startVideoCall() {
        // Your existing video call logic using Agora SDK
        Toast.makeText(groupsMemberActivity.this, "Starting video call...", Toast.LENGTH_SHORT).show();
    }
}