package cz.jindrichspacekv.norton360_dashboard.di

import cz.jindrichspacekv.norton360_dashboard.data.MockSecurityRepositoryImpl
import cz.jindrichspacekv.norton360_dashboard.data.SecurityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindSecurityRepository(
        impl: MockSecurityRepositoryImpl
    ): SecurityRepository
}
