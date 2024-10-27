package com.app.softwarehousemanager.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.softwarehousemanager.activities.GroupChatActivity
import com.app.softwarehousemanager.databinding.AdapterGroupBinding
import com.app.softwarehousemanager.models.ChatMessage
import com.app.softwarehousemanager.models.GroupMessage
import com.app.softwarehousemanager.models.User
import com.app.softwarehousemanager.models.UserGroup
import com.app.softwarehousemanager.services.AuthService

class GroupsAdapter(
    private val activity:Activity,
    private val groups: List<Pair<User, UserGroup>>,
    private val onJoinGroup:(UserGroup) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(private val binding: AdapterGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(activity: Activity, group: Pair<User, UserGroup>,onJoinGroup:(UserGroup) -> Unit) {
            binding.tvGroupName.text = group.second.groupName
            binding.tvCreatedBy.text = "Created By: ${group.first.name}"
            binding.tvTotalMembers.text = "Group Users: ${group.second.members.size}"

            binding.root.setOnClickListener {
                if(group.second.members.contains(AuthService().getCurrentUserId())){
                    activity.startActivity(Intent(activity, GroupChatActivity::class.java)
                        .putExtra("groupId", group.second.groupId)
                        .putExtra("groupName", group.second.groupName))
                }else{
                    binding.layoutJoinGroup.visibility = View.VISIBLE
                }
            }

            binding.btnCancelJoinGroup.setOnClickListener {
                binding.layoutJoinGroup.visibility = View.GONE
            }

            binding.btnJoinGroup.setOnClickListener {
                binding.btnJoinGroup.visibility = View.GONE
                binding.btnCancelJoinGroup.visibility = View.GONE
                binding.progressJoinGroup.visibility = View.VISIBLE
                onJoinGroup(group.second)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = AdapterGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(activity, groups[position],onJoinGroup)
    }

    override fun getItemCount(): Int = groups.size
}
