package org.example

import kotlinx.coroutines.runBlocking
import logic.usecase.auth.CreateDefaultAdminUseCase
import org.example.data.DataProvider
import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCaseModule
import org.example.entity.UserEntity
import org.example.presentation.PlanMateConsoleUI
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin


fun main() {
    startKoin {
        modules(
            appModule,
            useCaseModule,
            uiModule,
            mongoModule
        )
    }
    val console: PlanMateConsoleUI = getKoin().get()
    val usersData: DataProvider<UserEntity> = getKoin().get(named("userDataProviderMongo"))
    val createDefaultAdminUseCase = CreateDefaultAdminUseCase(usersData)
    runBlocking {
        try {
            createDefaultAdminUseCase.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        console.start()
    }
}