package org.example

import kotlinx.coroutines.runBlocking
import org.example.di.appModule
import org.example.di.mongoModule
import org.example.di.uiModule
import org.example.di.useCaseModule
import org.example.presentation.PlanMateConsoleUI
import org.koin.core.context.startKoin
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
    runBlocking {
        console.start()
    }
}