package com.example.ms_whatsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.modal.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<MyChatListHolder>() {

    var listOfChats = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)
        return MyChatListHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }

    fun setOnRecentChatList(list: List<RecentChats>) {
        this.listOfChats = list
        notifyDataSetChanged() // Notify adapter that data has changed
    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {
        val recentchatlist = listOfChats[position]

        holder.userName.text = recentchatlist.name

        val themessage = recentchatlist.message?.split(" ")?.take(4)?.joinToString(" ") ?: "No message"
        val makelastmessage = "${recentchatlist.person}: $themessage"

        holder.lastMessage.text = makelastmessage

        Glide.with(holder.itemView.context)
            .load(recentchatlist.friendsimage ) // Use a placeholder image if URL is null
            .into(holder.imageView)

        holder.timeView.text = recentchatlist.time?.substring(0, 5) ?: "N/A"

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatCLicked(position, recentchatlist)
        }
    }

    fun setOnRecentChatListener(listener: onRecentChatClicked) {
        this.listener = listener
    }
}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)
}

interface onRecentChatClicked {
    fun getOnRecentChatCLicked(position: Int, chatList: RecentChats)
}
