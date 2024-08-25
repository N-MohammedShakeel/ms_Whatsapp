package com.example.ms_whatsapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.Utils
import com.example.ms_whatsapp.adapter.MessageAdapter
import com.example.ms_whatsapp.databinding.FragmentChatBinding
import com.example.ms_whatsapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    lateinit var users: ChatFragmentArgs
    lateinit var binding: FragmentChatBinding
    lateinit var viewModel: ChatAppViewModel
    lateinit var adapter: MessageAdapter
    lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val textViewStatus = view.findViewById<TextView>(R.id.chatUserStatus)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        val users = ChatFragmentArgs.fromBundle(requireArguments()).users

        // Setting up the RecyclerView and its adapter
        val rvMessages: RecyclerView = view.findViewById(R.id.rvMessages)
        adapter = MessageAdapter() // Initialize the adapter

        // Setting up the LinearLayoutManager for the RecyclerView
        val layoutManager = LinearLayoutManager(activity)
        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = adapter

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Glide.with(view.context)
            .load(users.imageUrl)
            .placeholder(R.drawable.person)
            .into(circleImageView)

        textViewName.text = users.username
        textViewStatus.text = users.status

        chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homefragment)
        }

        binding.sendBtn.setOnClickListener {
            viewModel.sendMessage(Utils.getUidLoggedIn(),
                users.userid.toString(), users.username.toString(), users.imageUrl.toString()
            )
        }

        viewModel.getMessages(users.userid.toString()).observe(viewLifecycleOwner, Observer { messages ->
            messages?.let {
                Log.d("ChatFragment", "Messages received: ${messages.size}")
                adapter.setList(messages)
                rvMessages.scrollToPosition(adapter.itemCount - 1) // Scroll to bottom
            }
        })
    }
}
