package com.example.ms_whatsapp.fragments

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
import com.example.ms_whatsapp.databinding.FragmentChatfromHomeBinding
import com.example.ms_whatsapp.modal.Messages
import com.example.ms_whatsapp.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatfromHome : Fragment() {

    private lateinit var args: ChatfromHomeArgs
    private lateinit var binding: FragmentChatfromHomeBinding
    private lateinit var viewModel: ChatAppViewModel
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Toolbar and other views
        val toolbar = view.findViewById<Toolbar>(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Obtain arguments
        args = ChatfromHomeArgs.fromBundle(requireArguments())

        // Initialize RecyclerView and Adapter
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messagesRecyclerView.layoutManager = layoutManager
        binding.messagesRecyclerView.adapter = adapter
        layoutManager.stackFromEnd = true

        // Load user image and set details
        Glide.with(view.context)
            .load(args.recentchats.friendsimage)
            .placeholder(R.drawable.person)
            .dontAnimate()
            .into(circleImageView)

        textViewName.text = args.recentchats.name

        // Handle back button click
        chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatfromHome_to_homefragment)
        }

        // Handle send button click
        binding.sendBtn.setOnClickListener {
            viewModel.sendMessage(
                Utils.getUidLoggedIn(),
                args.recentchats.friendid!!,
                args.recentchats.name!!,
                args.recentchats.friendsimage!!
            )
        }

        // Observe messages and update RecyclerView
        viewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer { messages ->
            if (messages != null) {
                adapter.setList(messages)
            }
        })
    }
}
