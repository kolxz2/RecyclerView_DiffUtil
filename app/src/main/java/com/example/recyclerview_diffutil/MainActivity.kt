package com.example.recyclerview_diffutil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerview_diffutil.databinding.ActivityMainBinding
import com.example.recyclerview_diffutil.model.User
import ua.cn.stu.recyclerview.model.UsersListener
import ua.cn.stu.recyclerview.model.UsersService


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsersAdapter


    private val usersService: UsersService
        get() = (application as App).usersService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UsersAdapter(object : UserActionListener{
            /* implement interface for itemView action*/
            override fun userMove(user: User, move: Int){
                usersService.moveUser(user, move)
            }

            override fun onUserDelete(user: User){
                usersService.deleteUser(user)
            }

            override fun onUserDetails(user: User){
                Toast.makeText(
                    this@MainActivity,
                    "Name: ${user.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onUserFire(user: User) {
                usersService.fireUser(user)
            }


        })

        val layoutManager = LinearLayoutManager(this)
        // layoutManager talk how draw list (vertical)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        // turn off animation then itemView data is changed
        val itemAnimator = binding.recyclerView.itemAnimator
        if (itemAnimator is DefaultItemAnimator)
            itemAnimator.supportsChangeAnimations = false

        usersService.addListener(usersListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(usersListener)
    }

    private val usersListener: UsersListener = {
        adapter.users = it
    }
}