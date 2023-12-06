package net.primal.android.wallet.api.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.primal.android.networking.di.PrimalWalletApiClient
import net.primal.android.networking.primal.PrimalApiClient
import net.primal.android.nostr.notary.NostrNotary
import net.primal.android.wallet.api.WalletApi
import net.primal.android.wallet.api.WalletApiImpl

@Module
@InstallIn(SingletonComponent::class)
object WalletApiModule {
    @Provides
    fun provideWalletApi(
        @PrimalWalletApiClient primalApiClient: PrimalApiClient,
        nostrNotary: NostrNotary,
    ): WalletApi =
        WalletApiImpl(
            primalApiClient = primalApiClient,
            nostrNotary = nostrNotary,
        )
}
