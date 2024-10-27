package com.app.softwarehousemanager.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.softwarehousemanager.databinding.AdapterRecieverMessageBinding
import com.app.softwarehousemanager.databinding.AdapterSenderMessageBinding
import com.app.softwarehousemanager.models.ChatMessage
import com.app.softwarehousemanager.models.User

class ChatMessageAdapter (
    private val groupMessages: List<Triple<Boolean,User, ChatMessage>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENDER = 1
    private val VIEW_TYPE_RECEIVER = 2

    inner class SenderViewHolder(val binding: AdapterSenderMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(groupMessage: Triple<Boolean,User, ChatMessage>) {
            binding.tvMessage.text = groupMessage.third.text
            binding.tvSenderName.text = groupMessage.second.name
            binding.tvCreatedDate.text = groupMessage.third.createdAt
        }
    }

    inner class ReceiverViewHolder(private val binding: AdapterRecieverMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groupMessage: Triple<Boolean,User, ChatMessage>) {
            binding.tvMessage.text = groupMessage.third.text
            binding.tvSenderName.text = groupMessage.second.name
            binding.tvCreatedDate.text = groupMessage.third.createdAt
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = groupMessages[position]
        if (holder is SenderViewHolder) {
            holder.bind(message)
        } else if (holder is ReceiverViewHolder) {
            holder.bind(message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_SENDER) {
            val binding = AdapterSenderMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SenderViewHolder(binding)
        } else {
            val binding = AdapterRecieverMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceiverViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = groupMessages.size


    override fun getItemViewType(position: Int): Int {
        return if (groupMessages[position].first) VIEW_TYPE_SENDER else VIEW_TYPE_RECEIVER
    }
}


