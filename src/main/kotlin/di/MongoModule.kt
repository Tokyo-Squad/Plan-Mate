package org.example.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.UuidRepresentation
import org.koin.dsl.module

val mongoModule = module {
    single {
        val connectionString =
            "mongodb+srv://<your-username>:<your-password>@cluster0.watzb0c.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
        val databaseName = "planMate"

        val settings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(ConnectionString(connectionString))
            .build()

        val client = MongoClient.create(settings)
        client.getDatabase(databaseName)
    }
}