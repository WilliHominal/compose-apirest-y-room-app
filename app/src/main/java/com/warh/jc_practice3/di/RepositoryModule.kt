package com.warh.jc_practice3.di

import com.warh.jc_practice3.repository.UserRepository
import com.warh.jc_practice3.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun userRepository(repo: UserRepositoryImpl): UserRepository

}
