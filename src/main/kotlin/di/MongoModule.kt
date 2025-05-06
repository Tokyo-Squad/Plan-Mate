package org.example.di

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.bson.UuidRepresentation
import org.example.data.mongo.MongoDBClient
import org.koin.dsl.module
import java.util.*

val mongoModule = module {
    single {
        val properties = Properties().apply {
            javaClass.classLoader.getResourceAsStream("local.properties")?.use { inputStream ->
                load(inputStream)
            } ?: throw IllegalStateException("Could not load local.properties")
        }

        val username = properties.getProperty("mongodb.username")
        val password = properties.getProperty("mongodb.password")

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            throw IllegalStateException("Username or password not found in local.properties")
        }
        MongoDBClient(username, password)
    }

    single {
        get<MongoDBClient>().getDatabase()
    }
}