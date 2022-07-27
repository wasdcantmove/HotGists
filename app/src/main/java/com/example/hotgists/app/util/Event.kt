package com.example.hotgists.app.util

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Used as a wrapper for data that is exposed via LiveData that represents an event.
 */

open class Event<out T>(val content: T) {

    private val hasBeenHandled = AtomicBoolean(false)

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? =
        content.takeIf { hasBeenHandled.compareAndSet(false, true) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (content != other.content) return false
        if (hasBeenHandled.get() != other.hasBeenHandled.get()) return false

        return true
    }

    fun peek(): T = content

    override fun hashCode(): Int {
        var result = content?.hashCode() ?: 0
        result = 31 * result + hasBeenHandled.hashCode()
        return result
    }
}

typealias EmptyEventLiveData = MutableLiveData<Event<Unit>>

fun MutableLiveData<Event<Unit>>.trigger() {
    postValue(Event(Unit))
}

fun <T> MutableLiveData<Event<T>>.trigger(newValue: T) {
    postValue(Event(newValue))
}