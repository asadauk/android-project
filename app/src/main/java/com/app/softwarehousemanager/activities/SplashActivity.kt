package com.app.softwarehousemanager.activities
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.services.AuthService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Use lifecycleScope to handle coroutine delay
        lifecycleScope.launch {
            delay(3000) // 3 seconds delay

            if(AuthService().getCurrentUserId() != null){
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish() // Close SplashActivity
        }
    }
}
