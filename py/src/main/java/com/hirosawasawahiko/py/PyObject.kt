package com.hirosawasawahiko.py

import com.hirosawasawahiko.py.Utils.notnull
import com.hirosawasawahiko.py.Utils.irreversible
import com.hirosawasawahiko.py.Utils.readableUntil
import java.lang.ref.WeakReference

abstract class PyObject
private constructor(belonging: Director, parent: Group?,
                    width: Int, height: Int, dummy: Boolean) {

    constructor(belonging: Director, parent: Group, width: Int, height: Int)
            : this(belonging, parent, width, height, true)

    var parent by notnull<PyObject, Group>(parent) { it as Group }
        private set

    val belonging by readableUntil(belonging, { isAlive }) {
        throw RuntimeException("$this is no longer available.")
    }

    private val viewAttachListeners
            = mutableListOf<ViewAttachListener>()

    private var viewRef: WeakReference<Any>? = null

    internal val view; get() = viewRef?.get()

    val id = ObjectID.obtainNew()

    val isRoot; get() = parent.id == id

    val isNotRoot; get() = !isRoot

    var isAlive by irreversible(false); private set

    val isDead; get() = !isAlive

    var width by live<Int>()
    var height by live<Int>()
    var x by live<Int>()
    var y by live<Int>()
    var z by live<Int>()
    var alpha by live<Float>()

    init {
        this.width = width
        this.height = height
        x = 0
        y = 0
        z = 0
        alpha = 1f
    }

    fun destroySelf() {
        parent._children.remove(id)
        isAlive = false
    }

    fun attach(view: Any) {
        viewRef = WeakReference(view)
        viewAttachListeners.forEach { it.onViewAttached() }
    }

    fun moveTo(group: Group) {
        if (this.isRoot) throw IllegalStateException(
            "A root object cannot be a child of any other object.")
        if (this.isDead) throw IllegalStateException(
            "$this is no longer available.")
        if (group.isDead) throw IllegalArgumentException(
            "$group is no longer available.")
        if (group.id == id) throw IllegalArgumentException(
            "An object cannot be a child of itself.")

        parent._children.remove(id)
        parent = group
        parent._children[id] = this
    }

    abstract class Group(
        belonging: Director, parent: Group?, width: Int, height: Int)
    : PyObject(belonging, parent, width, height, true) {

        val children: Map<Long, PyObject>; get() = _children
        internal val _children = mutableMapOf<Long, PyObject>()
    }

    /**
     * Utility methods
     */

    internal interface ViewAttachListener {
        fun onViewAttached()
    }

    protected fun <T: Any> live() =
        LivePropertyDelegate<T>(this).apply {
            viewAttachListeners.add(this)
        }
}