package com.project.softwarehouseapplication;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class SplashActivity extends AppCompatActivity {

    // Define the views
    ImageView splashLogo;
    TextView splashAppName;

    // Duration for the splash screen (e.g., 2 seconds)
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2000 milliseconds

    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the splash screen layout
        setContentView(R.layout.activity_splash);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Find the ImageView and TextView by their IDs
        splashLogo = findViewById(R.id.splashLogo);
        splashAppName = findViewById(R.id.appName);

        // Find the root view (LinearLayout)
        View rootView = findViewById(R.id.splash_root_view);

        // Load and apply the fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        rootView.startAnimation(fadeIn);

        // Handler to delay the transition to the next activity
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, navigate to groupActivity
                Intent mainIntent = new Intent(SplashActivity.this, groupsMemberActivity.class);
                startActivity(mainIntent);
            } else {
                // No user is logged in, navigate to MainActivity (login/register)
                Intent loginIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
            // Close SplashActivity
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}