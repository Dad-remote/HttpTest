package pl.sergey.httptest.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.sergey.httptest.data.db.HttpTestDatabase
import pl.sergey.httptest.domain.repository.CallLogRepository
import pl.sergey.httptest.domain.repository.ServerRepository
import pl.sergey.httptest.data.repository.CallLogRepositoryImpl
import pl.sergey.httptest.data.repository.ServerRepositoryImpl
import pl.sergey.httptest.data.support.ActiveCallChecker
import pl.sergey.httptest.data.support.CallLogFetcher
import pl.sergey.httptest.data.support.IPHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    @Singleton
    fun provideIpHolder(@ApplicationContext context: Context): IPHolder = IPHolder(context)

    @Provides
    @Singleton
    fun provideCallLogFetcher(@ApplicationContext context: Context) = CallLogFetcher(context)

    @Provides
    @Singleton
    fun provideActiveCallChecker(@ApplicationContext context: Context) = ActiveCallChecker(context)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = HttpTestDatabase.build(context)

    @Provides
    @Singleton
    fun provideServerRepository(callLogFetcher: CallLogFetcher, database: HttpTestDatabase) : ServerRepository {
        return ServerRepositoryImpl(callLogFetcher, database)
    }

    @Provides
    @Singleton
    fun provideCallLogRepository(callLogFetcher: CallLogFetcher, database: HttpTestDatabase) : CallLogRepository {
        return CallLogRepositoryImpl(callLogFetcher, database)
    }
}