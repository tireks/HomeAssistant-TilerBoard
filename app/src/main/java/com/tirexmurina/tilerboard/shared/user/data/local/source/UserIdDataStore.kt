package com.tirexmurina.tilerboard.shared.user.data.local.source

class UserIdDataStore {
    private var userId: Long? = null

    fun set(userId: Long) {
        this.userId = userId
    }

    fun get() : Long? {
        return userId
    }
}