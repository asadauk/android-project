package com.app.softwarehousemanager.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.softwarehousemanager.activities.UserChatActivity
import com.app.softwarehousemanager.databinding.AdapterUserBinding
import com.app.softwarehousemanager.models.User

class UsersAdapter(
    private val users: List<User>,
    private val activity: Activity
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(private val binding: AdapterUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, activity: Activity) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email

            binding.root.setOnClickListener {
                activity.startActivity(Intent(activity, UserChatActivity::class.java)
                    .putExtra("userId",user.userId)
                    .putExtra("userName",user.name))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = AdapterUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position], activity)
    }

    override fun getItemCount(): Int = users.size
}
