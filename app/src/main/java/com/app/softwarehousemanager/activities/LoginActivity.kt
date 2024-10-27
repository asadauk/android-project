package com.app.softwarehousemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.databinding.ActivityLoginBinding
import com.app.softwarehousemanager.services.AuthService

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.loginButton.setOnClickListener {
            if(binding.emailInput.text.isBlank()){
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show()
            }else if(binding.passwordInput.text.isBlank()){
                Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
            }else{
                binding.loginButton.visibility = View.GONE
                binding.progressUserLogin.visibility = View.VISIBLE
                AuthService().loginUser( binding.emailInput.text.toString(),
                    binding.passwordInput.text.toString()
                 ) { loginSuccess ->

                    binding.loginButton.visibility = View.VISIBLE
                    binding.progressUserLogin.visibility = View.GONE

                    if(loginSuccess){
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this,
                            "Wrong credentials are given: Unable to login",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
            finish()
        }
    }
}