package net.primal.android.explore.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.primal.android.explore.api.ExploreApi
import net.primal.android.explore.api.ExploreApiImpl
import net.primal.android.networking.sockets.SocketClient

@Module
@InstallIn(SingletonComponent::class)
object ExploreModule {
    @Provides
    fun provideExploreApi(
        socketClient: SocketClient,
    ): ExploreApi = ExploreApiImpl(
        socketClient = socketClient,
    )

}