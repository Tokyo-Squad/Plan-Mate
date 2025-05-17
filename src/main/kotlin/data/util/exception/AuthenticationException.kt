package org.example.data.util.exception

open class AuthenticationException(message: String) : Exception(message) {

    class InvalidCredentials(message: String = "Username or password is incorrect.") :
        AuthenticationException(message)

    class UserAlreadyExists(message: String = "A user with that username already exists.") :
        AuthenticationException(message)

    class UserNotFound(message: String = "No user found with the given username.") :
        AuthenticationException(message)

    class NoCurrentUser(message: String = "There is no current authenticated user.") :
        AuthenticationException(message)
}