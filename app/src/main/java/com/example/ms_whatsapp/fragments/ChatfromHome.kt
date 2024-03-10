package com.example.ms_whatsapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    lateinit var args : ChatfromHomeArgs
    lateinit var binding: FragmentChatfromHomeBinding
    lateinit var chatAppViewModel : ChatAppViewModel
    lateinit var toolbar: Toolbar
    private lateinit var messageadapter : MessageAdapter
    private lateinit var chattoolbar: Toolbar
    lateinit var circleImageView: CircleImageView




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

        //same code as chatfragment with minor changes

        toolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = toolbar.findViewById(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)


        args = ChatfromHomeArgs.fromBundle(requireArguments())

        chattoolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = chattoolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val tvUsername = chattoolbar.findViewById<TextView>(R.id.chatUserName)
        val tvStatus = view.findViewById<TextView>(R.id.chatUserStatus)



        binding.chatBackBtn.setOnClickListener{
            view.findNavController().navigate(R.id.action_chatfromHome_to_homefragment)
        }

        Glide.with(requireContext()).load(args.recentchats.friendsimage!!).placeholder(R.drawable.person).dontAnimate().into(circleImageView)
        tvUsername.setText(args.recentchats.name)
        tvStatus.setText(args.recentchats.status)

        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        binding.viewModel = chatAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.sendBtn.setOnClickListener{
            chatAppViewModel.sendMessage(Utils.getUidLoggedIn(),args.recentchats.friendid!!,args.recentchats.name!! ,args.recentchats.friendsimage!!)
        }

        //user id of whom we gonna text
        chatAppViewModel.getMessages(args.recentchats.name!!).observe(viewLifecycleOwner, Observer{

            initRecyclerView(it)

        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(messages: List<Messages>) {
        messageadapter = MessageAdapter()

        val layoutManager = LinearLayoutManager(context)

        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        messageadapter.setMessageList(messages)
        messageadapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = messageadapter


    }

}