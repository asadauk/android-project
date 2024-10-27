package com.app.softwarehousemanager.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.adapters.AddGroupMemberAdapter
import com.app.softwarehousemanager.adapters.GroupMessageAdapter
import com.app.softwarehousemanager.adapters.GroupsAdapter
import com.app.softwarehousemanager.databinding.ActivityGroupChatBinding
import com.app.softwarehousemanager.databinding.ActivityLoginBinding
import com.app.softwarehousemanager.databinding.DialogAddGroupMemberBinding
import com.app.softwarehousemanager.models.GroupMessage
import com.app.softwarehousemanager.models.User
import com.app.softwarehousemanager.services.GroupChatService
import com.app.softwarehousemanager.services.UserService
import com.google.android.material.bottomsheet.BottomSheetDialog

class GroupChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var messagesList:ArrayList<Triple<Boolean, User, GroupMessage>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val groupId = intent.getStringExtra("groupId")!!
        messagesList = arrayListOf()
        loadMessages(groupId)


        binding.imgSend.setOnClickListener {
            GroupChatService().sendGroupMessage(groupId=groupId, messageText = binding.etMessage.text.toString()){
                binding.etMessage.text.clear()
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgAdd.setOnClickListener {
            showAddMemberDialog()
        }

        binding.tvGroupName.text = intent.getStringExtra("groupName")!!
    }

    private fun loadMessages(groupId: String) {
        binding.progerssLoadMessages.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        GroupChatService().loadGroupMessages(groupId=groupId){onMessagesLoaded ->
            messagesList.clear()
            messagesList.addAll(onMessagesLoaded)

            binding.progerssLoadMessages.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {

        val chatAdapter =GroupMessageAdapter(groupMessages = messagesList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)

    }

    private fun showAddMemberDialog() { // `users` is your data list
        val bottomSheetDialog = BottomSheetDialog(this)
        val binding = DialogAddGroupMemberBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.rvMembers.layoutManager = LinearLayoutManager(this)

        binding.rvMembers.visibility = View.GONE
        binding.progressLoadingUsers.visibility = View.VISIBLE
        val groupId = intent.getStringExtra("groupId")!!

        GroupChatService().getNonGroupUsers(groupId = groupId, onUsersReceived = {users ->

            binding.rvMembers.visibility = View.VISIBLE
            binding.progressLoadingUsers.visibility = View.GONE

            binding.rvMembers.adapter = AddGroupMemberAdapter(users) { member ->
                binding.rvMembers.visibility = View.GONE
                binding.progressLoadingUsers.visibility = View.VISIBLE
                GroupChatService().addGroupMember(
                    groupId = groupId, memberId = member.userId, onMemberAdded = {
                        binding.rvMembers.visibility = View.VISIBLE
                        binding.progressLoadingUsers.visibility = View.GONE
                        bottomSheetDialog.dismiss()
                    }
                )
            }

        }, onError = { error ->
            binding.rvMembers.visibility = View.VISIBLE
            binding.progressLoadingUsers.visibility = View.GONE
        })

        bottomSheetDialog.show()
    }


}