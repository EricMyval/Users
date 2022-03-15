package com.ericmyval.users.model

import com.ericmyval.users.UserNotFoundException
import com.ericmyval.users.model.coroutines.IODispatcher
import com.github.javafaker.Faker
import com.ericmyval.users.screens.base.Result
import com.ericmyval.users.screens.base.SuccessResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

typealias UsersListener = (users: List<User>) -> Unit

class UsersService(
    private val ioDispatcher: IODispatcher
) {
    private var users: MutableList<User> = mutableListOf()
    private val listeners = mutableSetOf<UsersListener>()

    fun listenUsers(): Flow<Result<List<User>>> = callbackFlow {
        val listener: UsersListener = {
            trySend(SuccessResult(it))
        }
        listeners.add(listener)
        awaitClose {
            listeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED) // последний реультат из буфера

    private fun notifyChanges() {
        listeners.forEach { it.invoke(users) }
    }

    suspend fun loadUsers() = withContext(ioDispatcher.value) {
        delay(1000)
        val faker = Faker.instance()
        IMAGES.shuffle()
        users = (1..50).map {
            User(
                id = it.toLong(),
                name = faker.name().name(),
                company = faker.company().name(),
                photo = IMAGES[it % IMAGES.size]
            )
        }.toMutableList()
        notifyChanges()
    }

    suspend fun getById(id: Long): UserDetails = withContext(ioDispatcher.value) {
        delay(1000)
        val user = users.firstOrNull { it.id == id } ?: throw UserNotFoundException()
        return@withContext UserDetails(
            user = user,
            details = Faker.instance().lorem().paragraphs(3).joinToString("\n\n")
        )
    }

    suspend fun deleteUser(user: User) = withContext(ioDispatcher.value) {
        delay(1000)
        val indexToDelete = users.indexOfFirst { it.id == user.id }
        if (indexToDelete != -1) {
            users.removeAt(indexToDelete)
            notifyChanges()
        }
    }
    suspend fun fireUser(user: User) = withContext(ioDispatcher.value) {
        delay(1000)
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            val update = users[index].copy(company = "")
            users = ArrayList(users)
            users[index] = update
            notifyChanges()
        }
    }
    suspend fun moveUser(user: User, moveBy: Int) = withContext(ioDispatcher.value) {
        delay(1000)
        val oldIndex = users.indexOfFirst { it.id == user.id }
        if (oldIndex == -1)
            return@withContext
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= users.size)
            return@withContext
        Collections.swap(users, oldIndex, newIndex)
        notifyChanges()
    }

    fun setCurrentColor(color: Int): Flow<Int> = flow {
        var progress = 0
        while (progress < 100) {
            progress += 1
            delay(10)
            emit(progress)
        }
    }.flowOn(ioDispatcher.value)

    companion object {
        private val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1600267185393-e158a98703de?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NjQ0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1579710039144-85d6bdffddc9?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Njk1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODE0&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1620252655460-080dbec533ca?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzQ1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1613679074971-91fc27180061?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzUz&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1485795959911-ea5ebf41b6ae?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzU4&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1545996124-0501ebae84d0?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0NzY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/flagged/photo-1568225061049-70fb3006b5be?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0Nzcy&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1567186937675-a5131c8a89ea?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODYx&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800",
            "https://images.unsplash.com/photo-1546456073-92b9f0a8d413?crop=entropy&cs=tinysrgb&fit=crop&fm=jpg&h=600&ixid=MnwxfDB8MXxyYW5kb218fHx8fHx8fHwxNjI0MDE0ODY1&ixlib=rb-1.2.1&q=80&utm_campaign=api-credit&utm_medium=referral&utm_source=unsplash_source&w=800"
        )
    }
}