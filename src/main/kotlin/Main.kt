package org.example

import logic.usecase.auth.CreateDefaultAdminUseCase
import org.example.di.appModule
import org.example.di.uiModule
import org.example.di.useCaseModule
import org.example.presentation.PlanMateConsoleUI
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin


fun main() {
    startKoin {
        modules(
            appModule,
            useCaseModule,
            uiModule
        )
    }
    CreateDefaultAdminUseCase(getKoin().get(named("userDataProvider")))
    val console: PlanMateConsoleUI = getKoin().get()
    console.start()
}