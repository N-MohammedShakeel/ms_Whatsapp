package com.example.ms_whatsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.modal.Users
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<Users>()
    private var listener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {

        val users = listOfUsers[position]
        val name = users.username!!.split("\\s".toRegex())[0] // we only need firstname of the username to set profilename so,removing lastname
        holder.profileName.setText(name)

        // Set status image based on user status
        when (users.status) {
            "Online" -> holder.statusImageView.setImageResource(R.drawable.onlinestatus)
            "Offline" -> holder.statusImageView.setImageResource(R.drawable.offlinestatus)
            else -> holder.statusImageView.setImageDrawable(null) // Clear image if status is unknown
        }

        Glide.with(holder.itemView.context)
            .load(users.imageUrl)
            .into(holder.imageProfile) // using glide for image processing

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnUserClickListener){
        this.listener = listener
    }

}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val profileName: TextView = itemView.findViewById(R.id.userName)
    val imageProfile : CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView: ImageView = itemView.findViewById(R.id.statusOnline)
}

interface OnUserClickListener{
    fun onUserSelected(position: Int, users: Users)
}