package com.example.ms_whatsapp.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ms_whatsapp.R
import com.example.ms_whatsapp.activities.SignInActivity
import com.example.ms_whatsapp.adapter.OnUserClickListener
import com.example.ms_whatsapp.adapter.RecentChatAdapter
import com.example.ms_whatsapp.adapter.UserAdapter
import com.example.ms_whatsapp.adapter.onRecentChatClicked
import com.example.ms_whatsapp.databinding.FragmentHomeBinding
import com.example.ms_whatsapp.modal.RecentChats
import com.example.ms_whatsapp.modal.Users
import com.example.ms_whatsapp.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class Homefragment : Fragment(), OnUserClickListener,onRecentChatClicked {

    lateinit var rvUsers : RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var binding: FragmentHomeBinding
    lateinit var auth:FirebaseAuth
    lateinit var firestore : FirebaseFirestore
    lateinit var toolbar: Toolbar
    lateinit var recentimage: CircleImageView
    lateinit var circleImageView: CircleImageView
    lateinit var recentadapter : RecentChatAdapter
    lateinit var rvRecentChats : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        rvUsers = binding.rvUsers
        rvRecentChats = binding.rvRecentChats

        userAdapter = UserAdapter()
        recentadapter = RecentChatAdapter()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)

        rvUsers.layoutManager = layoutManager
        rvRecentChats.layoutManager= layoutManager2


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        toolbar = view.findViewById(R.id.toolbarMain)
        val logoutimage = toolbar.findViewById<ImageView>(R.id.logOut)
        circleImageView = toolbar.findViewById(R.id.tlImage)


        binding.lifecycleOwner = viewLifecycleOwner

        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {

            Glide.with(requireContext()).load(it).into(circleImageView)

        })

        firestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

        logoutimage.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))

        }


        rvUsers.adapter = userAdapter
        val isAdapterAttached = rvUsers.adapter != null

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {

            userAdapter.setList(it)


        })
        userAdapter.setOnClickListener(this)

        rvRecentChats.adapter = recentadapter
        val isAdapterAttached1 = rvRecentChats.adapter != null

        userViewModel.getRecentUsers().observe(viewLifecycleOwner, Observer { recentChats ->
            if (recentChats != null) {
                recentadapter.setOnRecentChatList(recentChats)
            } else {
                recentadapter.setOnRecentChatList(emptyList())
            }
        })
        recentadapter.setOnRecentChatListener(this)

        Log.d("ChatFragment", "Chat ,Adapter is attached: $isAdapterAttached,$isAdapterAttached1")




        circleImageView.setOnClickListener {

            view.findNavController().navigate(R.id.action_homefragment_to_settingFragment)

        }
        userAdapter.setOnClickListener(this)


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onUserSelected(position: Int, users: Users) {
                // To use FragmentDirections , apply id("androidx.navigation.safeargs.kotlin") in module gradle
        val action = HomefragmentDirections.actionHomefragmentToChatFragment(users)// inorder to pass users which consist of multiple objects we have to create an argument in the graph inside the chatfragment and suppress the whole file
        view?.findNavController()?.navigate(action)

        Log.d("HomeFragment","ClickedOn${users.username}")

    }

    override fun getOnRecentChatCLicked(position: Int, recentchats: RecentChats) {
        val action = HomefragmentDirections.actionHomefragmentToChatfromHome(recentchats)
        view?.findNavController()?.navigate(action)
    }

}