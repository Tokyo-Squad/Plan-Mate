package org.example.data.csvfile

import org.example.data.AuthProvider
import org.example.entity.UserEntity

class AuthProviderImpl(
    fileName: String
) : AuthProvider {
    override fun addCurrentUser(user: UserEntity) {
    }

    override fun deleteCurrentUser() {
    }

    override fun getCurrentUser(): UserEntity? {
        return null
    }

}
