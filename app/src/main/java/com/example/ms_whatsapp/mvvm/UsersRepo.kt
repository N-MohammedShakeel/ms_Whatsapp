package com.example.ms_whatsapp.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.modal.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private val firestore = FirebaseFirestore.getInstance()


    fun getUsers(): LiveData<List<Users>> {
        val users = MutableLiveData<List<Users>>()
        firestore.collection("Users").addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                return@addSnapshotListener
            }

            // all the user added except who logged in
            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->
                // making object for each document
                val user = document.toObject(Users::class.java)
                if (user!!.userid != Utils.getUidLoggedIn()) {
                    user.let {
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }
}