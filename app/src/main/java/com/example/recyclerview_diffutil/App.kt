package com.example.recyclerview_diffutil

import android.app.Application
import ua.cn.stu.recyclerview.model.UsersService

// singleton App class
class App : Application() {
    // from any place of project can call usersService
    val usersService = UsersService()
}