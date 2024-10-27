package com.app.softwarehousemanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.adapters.GroupsAdapter
import com.app.softwarehousemanager.adapters.UsersAdapter
import com.app.softwarehousemanager.databinding.ActivityDashboardBinding
import com.app.softwarehousemanager.models.User
import com.app.softwarehousemanager.models.UserGroup
import com.app.softwarehousemanager.services.AuthService
import com.app.softwarehousemanager.services.UserService
import com.app.softwarehousemanager.services.GroupChatService
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var groupList: List<Pair<User, UserGroup>>
    private var activeTab : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.imgMenu.setOnClickListener{view ->
            showPopupMenu(view)
        }

        getAllUsers()

        binding.usersRefreshLayout.setOnRefreshListener {
            if(activeTab == 1)
                getAllGroups()
            else
                getAllUsers()
        }

        binding.tvUsersTab.setOnClickListener {
            activeTab = 0
            getAllUsers()
        }

        binding.tvGroupsTab.setOnClickListener {
            activeTab = 1
            getAllGroups()
        }
    }

    override fun onResume() {
        super.onResume()
        getAllUsers()
    }

    private fun showPopupMenu(view: View) {
        // Create the PopupMenu
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)

        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_create_group -> {
                    startActivity(Intent(this, CreateGroupActivity::class.java))

                    true
                }
                R.id.action_log_out -> {
                    AuthService().logoutUser{
                        startActivity(Intent(this, LoginActivity::class.java))
                        finishAffinity()
                    }
                    true
                }
                else -> false
            }
        }

        // Show the PopupMenu
        popupMenu.show()
    }

    private fun getAllGroups() {
        binding.layoutGroupTab.visibility = View.VISIBLE
        binding.progressLoadingUsers.visibility = View.VISIBLE
        binding.rvUsers.visibility = View.GONE
        binding.layoutUserTab.visibility = View.GONE

        lifecycleScope.launch {
            GroupChatService().loadGroupsWithCreators {userGroups ->
                groupList = userGroups
                setGroupToRecycler()
            }
        }
    }

    private fun getAllUsers() {
        binding.layoutUserTab.visibility = View.VISIBLE
        binding.progressLoadingUsers.visibility = View.VISIBLE
        binding.layoutGroupTab.visibility = View.GONE
        binding.rvUsers.visibility = View.GONE
        UserService().getAllUsers(
            onUsersReceived = { users ->

                if(users.isNotEmpty()) {
                    binding.noUserFoundTv.visibility = View.GONE
                    binding.progressLoadingUsers.visibility = View.GONE
                    binding.rvUsers.visibility = View.VISIBLE

                    setUsersToRecycler(users)
                }else{
                    binding.noUserFoundTv.visibility = View.VISIBLE
                    binding.rvUsers.visibility = View.GONE
                    binding.noUserFoundTv.text = "No users found at the moment"

                    binding.progressLoadingUsers.visibility = View.GONE
                }
            },

            onError = { error ->
                println("Error: ${error.message}")
                binding.noUserFoundTv.visibility = View.VISIBLE
                binding.rvUsers.visibility = View.GONE
                binding.noUserFoundTv.text = "No users found at the moment"

                binding.progressLoadingUsers.visibility = View.GONE
            }
        )
    }

    private fun setUsersToRecycler(users: List<User>) {
        binding.progressLoadingUsers.visibility = View.GONE
        binding.rvUsers.visibility = View.VISIBLE

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = UsersAdapter(users = users, activity = this)
    }

    private fun setGroupToRecycler() {
        binding.progressLoadingUsers.visibility = View.GONE
        binding.rvUsers.visibility = View.VISIBLE

        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = GroupsAdapter(this,groupList){ group ->
            GroupChatService().addGroupMember(groupId = group.groupId, memberId = null){
                if(it){
                    startActivity(Intent(this, GroupChatActivity::class.java).
                    putExtra("groupId",group.groupId).
                    putExtra("groupName",group.groupName))

                }else{
                    Toast.makeText(this, "Unable join this group", Toast.LENGTH_SHORT).show()
                }
                setGroupToRecycler()
            }
        }
    }
}