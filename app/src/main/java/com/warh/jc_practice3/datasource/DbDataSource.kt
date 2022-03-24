package com.warh.jc_practice3.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.warh.jc_practice3.model.User
import com.warh.jc_practice3.model.UserDao

@Database(entities = [User::class], version = 1)
abstract class DbDataSource: RoomDatabase() {

    abstract  fun userDao(): UserDao
}