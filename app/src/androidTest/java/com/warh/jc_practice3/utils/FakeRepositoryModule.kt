package com.warh.jc_practice3.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.warh.jc_practice3.di.RepositoryModule
import com.warh.jc_practice3.model.User
import com.warh.jc_practice3.repository.UserRepository
import com.warh.jc_practice3.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
class FakeRepositoryModule {

    @Singleton
    @Provides
    fun userRepository(): UserRepository = object : UserRepository{
        private val users = MutableLiveData<List<User>>(emptyList())

        override suspend fun getNewUser(): User {
            var userList = users.value!!
            val newUser = User(
                "Name ${userList.size}",
                "LastName ${userList.size}",
                "City",
                "Image",
            )
            users.postValue(users.value?.toMutableList()?.apply { add(newUser) })
            return newUser
        }

        override suspend fun deleteUser(toDelete: User) {
            users.postValue(users.value?.toMutableList()?.apply { remove(toDelete) })
        }

        override fun getAllUser(): LiveData<List<User>> {
            return users
        }

    }
}