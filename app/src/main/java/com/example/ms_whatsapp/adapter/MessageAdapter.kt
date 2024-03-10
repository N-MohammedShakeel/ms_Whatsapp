package com.example.ms_whatsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.modal.Messages


class LeftMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val lmessageText: TextView = itemView.findViewById(R.id.show_message)
    private val ltimeOfSent: TextView = itemView.findViewById(R.id.timeView)

    fun bindMessage(message: String, time: String) {
        lmessageText.visibility = View.VISIBLE
        ltimeOfSent.visibility = View.VISIBLE
        lmessageText.text = message
        ltimeOfSent.text = time.substring(0, minOf(time.length, 5))
    }
}

class RightMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val messageText: TextView = itemView.findViewById(R.id.rightshow_message)
    private val timeOfSent: TextView = itemView.findViewById(R.id.righttimeView)

    fun bindMessage(message: String, time: String) {
        messageText.visibility = View.VISIBLE
        timeOfSent.visibility = View.VISIBLE
        messageText.text = message
        timeOfSent.text = time.substring(0, minOf(time.length, 5))
    }
}

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listOfMessage = listOf<Messages>()

    private val RIGHT = 1
    private val LEFT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chatitemright, parent, false)
            RightMessageViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.chatitemleft, parent, false)
            LeftMessageViewHolder(view)
        }
    }


    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listOfMessage[position].sender == Utils.getUidLoggedIn()) {
            RIGHT
        } else {
            LEFT
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMessageList(newList: List<Messages>) {
        this.listOfMessage = newList.toList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = listOfMessage[position]
        when (holder.itemViewType) {
            RIGHT -> {
                val rightViewHolder = holder as RightMessageViewHolder
                rightViewHolder.bindMessage(message.message ?: "", message.time ?: "")
            }

            LEFT -> {
                val leftViewHolder = holder as LeftMessageViewHolder
                leftViewHolder.bindMessage(message.message ?: "", message.time ?: "")
            }
        }
    }
}