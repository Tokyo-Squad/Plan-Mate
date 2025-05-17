package org.example.data.remote.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.UuidRepresentation

class MongoDBClient(
    username: String,
    password: String,
    clusterUrl: String = "cluster0.watzb0c.mongodb.net",
    databaseName: String = "PlanMate"
) {

    private val client: MongoClient
    private val database: MongoDatabase

    init {
        val connectionString = "mongodb+srv://$username:$password@$clusterUrl/?retryWrites=true&w=majority&appName=Cluster0"

        val settings = MongoClientSettings.builder()
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .applyConnectionString(ConnectionString(connectionString))
            .build()

        client = MongoClient.create(settings)
        database = client.getDatabase(databaseName)
    }

    fun getDatabase(): MongoDatabase {
        return database
    }

    fun close() {
        client.close()
    }
}
