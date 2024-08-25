package com.example.ms_whatsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.modal.Messages

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage: List<Messages> = listOf()
    private val LEFT = 0
    private val RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == RIGHT) {
            inflater.inflate(R.layout.chatitemright, parent, false)
        } else {
            inflater.inflate(R.layout.chatitemleft, parent, false)
        }
        return MessageHolder(view)
    }

    override fun getItemCount(): Int = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]
        holder.messageText.text = message.message ?: "No message"
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
    }

    override fun getItemViewType(position: Int): Int {
        // Assuming Utils.getUidLoggedIn() returns the current user's ID
        return if (listOfMessage[position].sender == Utils.getUidLoggedIn()) RIGHT else LEFT
    }

    fun setList(newList: List<Messages>) {
        this.listOfMessage = newList
        notifyDataSetChanged() // Notify the adapter that data has changed
    }
}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
}
