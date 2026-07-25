package com.zhr.blog.di

import com.zhr.blog.data.repository.BlogRepository
import com.zhr.blog.data.repository.BlogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBlogRepository(impl: BlogRepositoryImpl): BlogRepository
}