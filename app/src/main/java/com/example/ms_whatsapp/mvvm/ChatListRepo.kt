package com.example.ms_whatsapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.modal.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListRepo() {
    val firestore = FirebaseFirestore.getInstance()
    fun getAllChatList(): LiveData<List<RecentChats>> {

        val mainChatList = MutableLiveData<List<RecentChats>>()

        // SHOWING THE RECENT MESSAGED PERSON ON TOP
        firestore.collection("Conversation${Utils.getUidLoggedIn()}").orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    return@addSnapshotListener
                }

                val chatlist = mutableListOf<RecentChats>()
                snapshot?.forEach { document ->
                    val recentmodel = document.toObject(RecentChats::class.java)
                    if (recentmodel.sender.equals(Utils.getUidLoggedIn())) {
                        recentmodel.let {
                            chatlist.add(it)
                        }
                    }
                }
                mainChatList.value = chatlist
            }
        return mainChatList
    }
}