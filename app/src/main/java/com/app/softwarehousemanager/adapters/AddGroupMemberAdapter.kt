package com.app.softwarehousemanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.softwarehousemanager.databinding.AdapterAddGroupMemberBinding
import com.app.softwarehousemanager.models.User


class AddGroupMemberAdapter(
    private val users: List<User>,
    private val onAddClick: (User) -> Unit
) : RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding:AdapterAddGroupMemberBinding) : RecyclerView.ViewHolder(binding.root)  {

        fun bind(user: User) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.btnAddMember.setOnClickListener {
                onAddClick(user)
            }

            binding.root.setOnClickListener {
                binding.layoutAddMember.visibility = View.VISIBLE
            }

            binding.btnCancelAddMember.setOnClickListener {
                binding.layoutAddMember.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterAddGroupMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size
}
