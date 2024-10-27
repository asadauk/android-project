import com.app.softwarehousemanager.constkeys.CurrentDateAndTime
import com.app.softwarehousemanager.models.ChatMessage
import com.app.softwarehousemanager.models.GroupMessage
import com.app.softwarehousemanager.models.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatService(private val recipientId: String) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId: String? = auth.currentUser?.uid

    fun sendMessage(messageText: String, onMessageSent:() -> Unit) {
        if (messageText.isNotEmpty() && currentUserId != null) {
            val message = ChatMessage(senderId = currentUserId, recipientId = recipientId, text = messageText, createdAt = CurrentDateAndTime.getFormattedDateTime())
            // Store messages in a unique path for the current user and recipient
            database.child("chats").child(currentUserId).child(recipientId).push().setValue(message)
            database.child("chats").child(recipientId).child(currentUserId).push().setValue(message).addOnSuccessListener {
                onMessageSent()
            }
        }
    }


    fun loadMessagesWith(onMessagesLoaded: (List<Triple<Boolean, User, ChatMessage>>) -> Unit) {
        currentUserId?.let { userId ->
            database.child("chats").
            child(userId).child(recipientId).
            addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Triple<Boolean, User, ChatMessage>>()

                    val messagesTasks = snapshot.children.map { messageSnapshot ->
                        val message = messageSnapshot.getValue(ChatMessage::class.java)
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
        } ?: onMessagesLoaded(emptyList())
    }


    fun deleteMessage(recipientId: String, messageId: String, onMessageDeleted: (Boolean) -> Unit) {
        // Remove the message from the sender's chat and the recipient's chat
        currentUserId?.let { userId ->
            val userChatPath = "chats/$userId/$recipientId/$messageId"
            val recipientChatPath = "chats/$recipientId/$userId/$messageId"

            val deleteUserChatTask = database.child(userChatPath).removeValue()
            val deleteRecipientChatTask = database.child(recipientChatPath).removeValue()

            // Combine the tasks to check if both deletions are successful
            Tasks.whenAllComplete(deleteUserChatTask, deleteRecipientChatTask)
                .addOnCompleteListener { task ->
                    onMessageDeleted(task.isSuccessful)
                }
        } ?: onMessageDeleted(false)
    }
}
