package com.example.ms_whatsapp.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ms_whatsapp.MyApplication
import com.example.ms_whatsapp.SharedPrefs
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.modal.Messages
import com.example.ms_whatsapp.modal.RecentChats
import com.example.ms_whatsapp.modal.Users
import com.example.ms_whatsapp.notifications.FirebaseService.Companion.token
import com.example.ms_whatsapp.notifications.entity.NotificationData
import com.example.ms_whatsapp.notifications.entity.PushNotification
import com.example.ms_whatsapp.notifications.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    val chatListRepo = ChatListRepo()


    init {
        getCurrentUser()
        getRecentUsers()
    }

    // here we get all the users
    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers() // here the getuser() holds the value of object for each document from the user repo
    }

    // here we get users information
    private fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext
        // To fetch the document of the user who logged in
        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->

                if (error != null){
                    return@addSnapshotListener
                }

                if (value!!.exists() || value != null) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!

                    val mySharedPrefs = SharedPrefs(context)
                    mySharedPrefs.setValue("username", users.username)

                }
            }
    }

    //to send messages
    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = MyApplication.instance.applicationContext
            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "timer" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            // sending message
            //creates a room for the sender & receiver and the sequence as follows
            firestore.collection("Messages").document(uniqueId.toString())
                .collection("chats").document(Utils.getTime()).set(hashMap)
                .addOnCompleteListener { task ->


                    // works for recent chats
                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashMapForRecent)
                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    //for notification work
                    firestore.collection("Tokens").document(receiver)
                        .addSnapshotListener { value, error ->

                            if (error != null){
                                return@addSnapshotListener
                            }

                            if (value != null && value.exists()) {

                                val tokenObject =
                                    value.toObject(com.example.ms_whatsapp.notifications.entity.Token::class.java)
                                token = tokenObject?.token!!
                                val loggedInUsername =
                                    mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                                if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                    if (message.value != null && receiver != null && token != null) {
                                        PushNotification(
                                            NotificationData(loggedInUsername, message.value!!),
                                            token!!
                                        ).also {
                                            sendNotification(it)
                                        }
                                    }
                                } else {
                                    Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                                }
                            }
                            if (task.isSuccessful) {
                                message.value = ""
                            }
                        }
                }
        }



    fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friendid)
    }

    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatListRepo.getAllChatList()

    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext
        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                }
            }

        val mysharedprefs = SharedPrefs(context)
        val friendid = mysharedprefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String,Any>("friendsImage" to imageUrl.value!!,"name" to name.value!! , "person" to name.value!!)

      if (friendid != null){
          firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn()).update(hashMapUpdate)

          firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid!!).update("person","you")

      }
    }

    private fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {

            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }

}








