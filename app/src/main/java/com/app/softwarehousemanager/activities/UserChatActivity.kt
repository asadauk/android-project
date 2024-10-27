package com.app.softwarehousemanager.activities

import ChatService
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.softwarehousemanager.R
import com.app.softwarehousemanager.adapters.ChatMessageAdapter
import com.app.softwarehousemanager.adapters.GroupMessageAdapter
import com.app.softwarehousemanager.databinding.ActivityUserChatBinding
import com.app.softwarehousemanager.models.ChatMessage
import com.app.softwarehousemanager.models.GroupMessage
import com.app.softwarehousemanager.models.User

class UserChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserChatBinding
    private lateinit var recipientId:String
    private lateinit var messagesList:ArrayList<Triple<Boolean, User, ChatMessage>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityUserChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recipientId = intent.getStringExtra("userId")!!
        binding.tvChatName.text = intent.getStringExtra("userName")!!

        getUserChat()

        binding.imgSend.setOnClickListener {
            if(binding.etMessage.text.isNotEmpty()){
                binding.imgSend.visibility = View.GONE
                binding.progressSendLoading.visibility = View.VISIBLE
                ChatService(recipientId).sendMessage(binding.etMessage.text.toString()){
                    binding.etMessage.text.clear()
                    binding.imgSend.visibility = View.VISIBLE
                    binding.progressSendLoading.visibility = View.GONE
                }
            }
        }

        binding.imgBack.setOnClickListener {
            finish()
        }
    }

    private fun getUserChat() {

        binding.progerssLoadMessages.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        ChatService(recipientId = recipientId).loadMessagesWith{messages ->
            messagesList = arrayListOf()
            messagesList.addAll(messages)

            binding.progerssLoadMessages.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        val chatAdapter = ChatMessageAdapter(groupMessages = messagesList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }
}