package com.example.ms_whatsapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.adapter.MessageAdapter
import com.example.ms_whatsapp.databinding.FragmentChatBinding
import com.example.ms_whatsapp.modal.Messages
import com.example.ms_whatsapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {
    lateinit var args: ChatFragmentArgs
    lateinit var chatbinding : FragmentChatBinding

    lateinit var chatAppviewModel : ChatAppViewModel
    lateinit var adapter : MessageAdapter
    lateinit var chattoolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        return chatbinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chattoolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = chattoolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val tvUserName = chattoolbar.findViewById<TextView>(R.id.chatUserName)
        val tvStatus = view.findViewById<TextView>(R.id.chatUserStatus)
        val chatBackBtn = chattoolbar.findViewById<ImageView>(R.id.chatBackBtn)

        chatAppviewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        args = ChatFragmentArgs.fromBundle(requireArguments())

        chatbinding.viewModel = chatAppviewModel
        chatbinding.lifecycleOwner = viewLifecycleOwner

        Glide.with(requireContext()).load(args.users.imageUrl!!).into(circleImageView);
        tvUserName.setText(args.users.username)
        tvStatus.setText(args.users.status)

        chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homefragment)

        }

        chatbinding.sendBtn.setOnClickListener {
             chatAppviewModel.sendMessage(Utils.getUidLoggedIn(), args.users.userid!!, args.users.username!!, args.users.imageUrl!!)

        }

        chatAppviewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {

            initRecyclerView(it)

        })
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        adapter.setMessageList(list)

        val layoutManager = LinearLayoutManager(context)

        chatbinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        adapter.notifyDataSetChanged()
        chatbinding.messagesRecyclerView.adapter = adapter

    }
}