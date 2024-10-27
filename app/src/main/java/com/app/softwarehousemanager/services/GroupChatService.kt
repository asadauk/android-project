package com.app.softwarehousemanager.services

import android.util.Log
import com.app.softwarehousemanager.constkeys.CurrentDateAndTime
import com.app.softwarehousemanager.models.GroupMessage
import com.app.softwarehousemanager.models.User
import com.app.softwarehousemanager.models.UserGroup
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupChatService {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId: String? = auth.currentUser?.uid

    fun createGroup(groupName: String, onGroupCreated: (String) -> Unit) {
        val groupId = database.child("groups").push().key ?: return
        val group = UserGroup(groupId, groupName, currentUserId ?: "", listOf(currentUserId ?: ""))
        database.child("groups").child(groupId).setValue(group).addOnCompleteListener {
            if (it.isSuccessful) {
                onGroupCreated("group created success")
            }else{
                onGroupCreated("unable to create group")

            }
        }
    }

    fun loadGroupsWithCreators(callback: (List<Pair<User, UserGroup>>) -> Unit) {
        database.child("groups").get().addOnSuccessListener { groupsSnapshot ->
            val groupList = mutableListOf<Pair<User, UserGroup>>()

            val groupTasks = groupsSnapshot.children.mapNotNull { groupSnapshot ->
                val userGroup = groupSnapshot.getValue(UserGroup::class.java)
                userGroup?.let {
                    // Create a Task for each user fetch
                    database.child("users").child(userGroup.createdBy)
                        .get().continueWithTask { userTask ->
                        val user = userTask.result?.getValue(User::class.java)
                        user?.let { creator ->
                            groupList.add(Pair(creator, it))
                        }
                        Tasks.forResult(null) // Return a completed task
                    }
                }
            }

            // Use whenAllSuccess to wait for all user fetches to complete
            Tasks.whenAllSuccess<Void>(groupTasks).addOnCompleteListener {
                callback(groupList)
            }
        }
    }

    fun deleteGroup(groupId: String, onGroupDeleted: (Boolean) -> Unit) {
        database.child("groups").child(groupId).get().addOnSuccessListener { snapshot ->
            val group = snapshot.getValue(UserGroup::class.java)
            if (group != null && group.createdBy == auth.currentUser?.uid) {
                database.child("groups").child(groupId).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onGroupDeleted(true)
                    } else {
                        onGroupDeleted(false)
                    }
                }
            } else {
                // User is not the creator of the group
                onGroupDeleted(false)
            }
        }.addOnFailureListener {
            // Handle possible errors when retrieving the group
            Log.e("group_fetch","error to fitch user single group delete")
            onGroupDeleted(false)
        }
    }

    fun addGroupMember(groupId: String,memberId: String?, onMemberAdded: (Boolean) -> Unit) {

        database.child("groups").child(groupId).child("members").get().addOnSuccessListener { dataSnapshot ->
            val nextIndex = dataSnapshot.childrenCount.toInt()
            val nextIndexPath = "members/$nextIndex"
            val newMemberId = memberId ?: currentUserId

            database.child("groups").
            child(groupId).updateChildren(mapOf(nextIndexPath to newMemberId))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onMemberAdded(true)
                    } else {
                        onMemberAdded(false)
                    }
                }
        }.addOnFailureListener { exception ->
            onMemberAdded(false)
        }
    }

    fun sendGroupMessage(groupId: String, messageText: String,onMessageSent: (Boolean) -> Unit) {
        if (messageText.isNotEmpty() && currentUserId != null) {
            val message = GroupMessage(senderId = currentUserId, groupId = groupId, text = messageText, createdAt = CurrentDateAndTime.getFormattedDateTime())
            database.child("groups").child(groupId).child("messages").push().setValue(message).addOnSuccessListener {
                onMessageSent(true)
            }.addOnFailureListener{
                onMessageSent(false)
            }
        }
    }

    fun loadGroupMessages(groupId: String, onMessagesLoaded: (List<Triple<Boolean,User,GroupMessage>>) -> Unit) {
        database.child("groups").child(groupId).child("messages").
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Triple<Boolean,User, GroupMessage>>()

                val messagesTasks = snapshot.children.map { messageSnapshot ->
                    val message = messageSnapshot.getValue(GroupMessage::class.java)
                    message?.let {
                        database.child("users").child(message.senderId)
                            .get().continueWithTask { userTask ->
                                val user = userTask.result?.getValue(User::class.java)
                                user?.let { creator ->
                                    messages.add(Triple(message.senderId == currentUserId, user,message))
                                }
                                Tasks.forResult(null) // Return a completed task
                            }
                    }
                }

                Tasks.whenAllSuccess<Void>(messagesTasks).addOnCompleteListener {
                    onMessagesLoaded(messages)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getNonGroupUsers(groupId:String, onUsersReceived: (List<User>) -> Unit, onError: (Exception) -> Unit){
        database.child("groups").child(groupId).child("members").get().addOnSuccessListener { dataSnapshot ->
            val groupMembers = dataSnapshot.value as? List<String>

            UserService().getAllUsers(
                onUsersReceived = {users: List<User> ->
                    var  filteredUsers:List<User> = users
                    if(groupMembers != null){
                        filteredUsers = users.filter { user ->
                            !groupMembers.contains(user.userId)
                        }
                    }
                    onUsersReceived(filteredUsers)
                },
                onError = {
                    onError(it.toException())
                }
            )
        }.addOnFailureListener { exception ->
            onError(exception)
        }

    }

    fun deleteGroupMessage(groupId: String, messageId: String, onMessageDeleted: (Boolean) -> Unit) {
        // Remove the message from the group messages in the database
        database.child("groups").child(groupId).child("messages").child(messageId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onMessageDeleted(true)
                } else {
                    onMessageDeleted(false)
                }
            }.addOnFailureListener {
                // Handle possible errors when trying to delete the message
                onMessageDeleted(false)
            }
    }
}
