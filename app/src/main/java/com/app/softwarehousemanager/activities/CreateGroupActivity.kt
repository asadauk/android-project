package com.app.softwarehousemanager.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.databinding.ActivityCreateGroupBinding
import com.app.softwarehousemanager.databinding.ActivityDashboardBinding
import com.app.softwarehousemanager.services.GroupChatService

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.createGroupButton.setOnClickListener {
            if(binding.etGroupName.text.isBlank()){
                Toast.makeText(this, "Please enter group name", Toast.LENGTH_LONG).show()
            }else{
                binding.progressCreateGroup.visibility = View.VISIBLE
                binding.createGroupButton.visibility = View.GONE
                GroupChatService().createGroup(groupName = binding.etGroupName.text.toString()){ groupCreate ->
                    Toast.makeText(this, groupCreate, Toast.LENGTH_LONG).show()

                    binding.progressCreateGroup.visibility = View.GONE
                    binding.createGroupButton.visibility = View.VISIBLE

                    if(groupCreate == "group created success"){
                        finish()
                    }
                }
            }
        }
    }
}