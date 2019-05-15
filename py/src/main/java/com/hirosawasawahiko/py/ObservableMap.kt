package com.hirosawasawahiko.py

import android.support.annotation.RequiresApi
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

class ObservableMap<K: Any, V: Any>(
    private val delegate: MutableMap<K, V> = mutableMapOf())
    : MutableMap<K, V> by delegate {

    var observer: Observer<K>? = null

    private val tmpSet = mutableSetOf<K>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ObservableMap<*, *>) return false

        if (delegate != other.delegate) return false
        if (observer != other.observer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = delegate.hashCode()
        result = 31 * result + (observer?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return delegate.toString()
    }

    override val values: MutableCollection<V>
        get() = ObservableCollection(delegate.values)

    override val keys: MutableSet<K>
        get() = ObservableSet(delegate.keys)

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = ObservableSet(delegate.entries.asSequence().map {
            ObservableEntry(it) as MutableMap.MutableEntry<K, V>
        }.toMutableSet())

    operator fun set(key: K, value: V) {
        println("set")
        val v = delegate[key]
        if (v == null) {
            delegate.set(key, value)
            notifyOnPut(key)
        } else if (v != value) {
            delegate.set(key, value)
            notifyOnChange(key)
        }
    }

    operator fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
        println("iterator")
        return ObservableIterator(delegate.iterator())
    }

    operator fun minusAssign(key: K) {
        delegate.minusAssign(key)
    }

    operator fun minusAssign(keys: Iterable<K>) {
        delegate.minusAssign(keys)
    }

    operator fun minusAssign(keys: Array<out K>) {
        delegate.minusAssign(keys)
    }

    operator fun minusAssign(keys: Sequence<K>) {
        delegate.minusAssign(keys)
    }

    operator fun plusAssign(pair: Iterable<Pair<K, V>>) {
        delegate.plusAssign(pair)
    }

    operator fun plusAssign(pair: Array<out Pair<K, V>>) {
        delegate.plusAssign(pair)
    }

    operator fun plusAssign(pair: Sequence<Pair<K, V>>) {
        delegate.plusAssign(pair)
    }

    operator fun plusAssign(map: Map<K, V>) {
        delegate.plusAssign(map)
    }

    override fun clear() {
        println("clear")
        delegate.clear()
    }

    override fun remove(key: K): V? {
        println("remove1")
        return delegate.remove(key)
    }

    @RequiresApi(24)
    override fun remove(key: K, value: V): Boolean {
        println("remove2")
        return delegate.remove(key, value)
    }

    override fun put(key: K, value: V): V? {
        println("put")
        return delegate.put(key, value)
    }

    override fun putAll(from: Map<out K, V>) {
        println("putAll")
        delegate.putAll(from)
    }

    fun putAll(pairs: Array<out Pair<K, V>>) {
        delegate.putAll(pairs)
    }

    fun putAll(pairs: Iterable<Pair<K, V>>) {
        delegate.putAll(pairs)
    }

    fun putAll(pairs: Sequence<Pair<K, V>>) {
        delegate.putAll(pairs)
    }

    @RequiresApi(24)
    override fun compute(key: K, remappingFunction: BiFunction<in K, in V?, out V?>): V? {
        return delegate.compute(key, remappingFunction)
    }

    @RequiresApi(24)
    override fun putIfAbsent(key: K, value: V): V? {
        return delegate.putIfAbsent(key, value)
    }

    @RequiresApi(24)
    override fun computeIfAbsent(key: K, mappingFunction: Function<in K, out V>): V {
        return delegate.computeIfAbsent(key, mappingFunction)
    }

    @RequiresApi(24)
    override fun computeIfPresent(key: K, remappingFunction: BiFunction<in K, in V, out V?>): V? {
        return delegate.computeIfPresent(key, remappingFunction)
    }

    @RequiresApi(24)
    override fun merge(key: K, value: V, remappingFunction: BiFunction<in V, in V, out V?>): V? {
        return delegate.merge(key, value, remappingFunction)
    }

    @RequiresApi(24)
    override fun replace(key: K, oldValue: V, newValue: V): Boolean {
        return delegate.replace(key, oldValue, newValue)
    }

    @RequiresApi(24)
    override fun replace(key: K, value: V): V? {
        return delegate.replace(key, value)
    }

    @RequiresApi(24)
    override fun replaceAll(function: BiFunction<in K, in V, out V>) {
        delegate.replaceAll(function)
    }

    private fun notifyOnChange(vararg keys: K)
            = notifyToObserver(keys) { observer?.onChange(it) }

    private fun notifyOnPut(vararg keys: K)
            = notifyToObserver(keys) { observer?.onPut(it) }

    private fun notifyOnRemove(vararg keys: K)
            = notifyToObserver(keys) { observer?.onRemoved(it) }

    private fun notifyOnChange(keys: Iterable<K>)
            = notifyToObserver(keys) { observer?.onChange(it) }

    private fun notifyOnPut(keys: Iterable<K>)
            = notifyToObserver(keys) { observer?.onPut(it) }

    private fun notifyOnRemove(keys: Iterable<K>)
            = notifyToObserver(keys) { observer?.onRemoved(it) }

    private fun notifyToObserver(
        keys: Array<out K>, notify: (keys: Set<K>) -> Unit) {
        tmpSet.clear()
        tmpSet.addAll(keys)
        notify(tmpSet)
    }

    private fun notifyToObserver(
        keys: Iterable<K>, notify: (keys: Set<K>) -> Unit) {
        tmpSet.clear()
        tmpSet.addAll(keys)
        notify(tmpSet)
    }

    private class ObservableCollection<T>(
        private val delegate: MutableCollection<T>)
        : MutableCollection<T> by delegate {



        override fun add(element: T): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addAll(elements: Collection<T>): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun clear() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun iterator(): MutableIterator<T> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun remove(element: T): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        @RequiresApi(24)
        override fun removeIf(filter: Predicate<in T>): Boolean {
            return delegate.removeIf(filter)
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ObservableCollection<*>) return false

            if (delegate != other.delegate) return false

            return true
        }

        override fun hashCode(): Int {
            return delegate.hashCode()
        }
    }

    private class ObservableSet<T>(
        private val delegate: MutableSet<T>)
        : MutableSet<T> by delegate {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ObservableSet<*>) return false

            if (delegate != other.delegate) return false

            return true
        }

        override fun hashCode(): Int {
            return delegate.hashCode()
        }

        override fun add(element: T): Boolean {
            return delegate.add(element)
        }

        override fun addAll(elements: Collection<T>): Boolean {
            return delegate.addAll(elements)
        }

        override fun clear() {
            delegate.clear()
        }

        override fun iterator(): MutableIterator<T> {
            return ObservableIterator(delegate.iterator())
        }

        override fun remove(element: T): Boolean {
            return delegate.remove(element)
        }

        override fun removeAll(elements: Collection<T>): Boolean {
            return delegate.removeAll(elements)
        }

        override fun retainAll(elements: Collection<T>): Boolean {
            return delegate.retainAll(elements)
        }
    }

    private class ObservableEntry<K, V>(
        private val delegate: MutableMap.MutableEntry<K, V>)
        : MutableMap.MutableEntry<K, V> by delegate {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ObservableEntry<*, *>) return false

            if (delegate != other.delegate) return false

            return true
        }

        override fun hashCode(): Int {
            return delegate.hashCode()
        }

        override fun setValue(newValue: V): V {
            return delegate.setValue(newValue)
        }
    }

    private class ObservableIterator<T>(
        private val delegate: MutableIterator<T>)
        : MutableIterator<T> by delegate {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ObservableIterator<*>) return false

            if (delegate != other.delegate) return false

            return true
        }

        override fun hashCode(): Int {
            return delegate.hashCode()
        }

        override fun remove() {
            delegate.remove()
        }
    }

    interface Observer<K> {
        fun onRemoved(keys: Set<K>)
        fun onPut(keys: Set<K>)
        fun onChange(keys: Set<K>)
    }
}