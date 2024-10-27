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
import com.app.softwarehousemanager.databinding.ActivityRegisterUserBinding
import com.app.softwarehousemanager.services.AuthService

class RegisterUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.registerButton.setOnClickListener {
            if(binding.nameInput.text.isBlank()){
                Toast.makeText(this, "Please enter full name", Toast.LENGTH_LONG).show()
            }else if(binding.emailInput.text.isBlank()){
                Toast.makeText(this, "Please enter email id", Toast.LENGTH_LONG).show()
            }else if(binding.passwordInput.text.isBlank()){
                Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
            }else if(binding.passwordInput.text.toString() != binding.confirmPasswordInput.text.toString()){
                Toast.makeText(this, "Passwords are not matched", Toast.LENGTH_LONG).show()
            }else{

                binding.registerButton.visibility = View.GONE
                binding.progressUserRegister.visibility = View.VISIBLE

                AuthService().registerUser(
                    email = binding.emailInput.text.toString(),
                    name = binding.nameInput.text.toString(),
                    password = binding.passwordInput.text.toString()){ userRegister->

                    binding.registerButton.visibility = View.VISIBLE
                    binding.progressUserRegister.visibility = View.GONE

                    if(userRegister){
                            Toast.makeText(this, "User successfully registered", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        }else{
                            Toast.makeText(this, "Unable to register user", Toast.LENGTH_LONG).show()

                        }
                    }
            }
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}