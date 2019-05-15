package com.hirosawasawahiko.py

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class LivePropertyDelegate<T: Any>(
    private val belonging: PyObject)
    : ReadWriteProperty<Any, T>
    , PyObject.ViewAttachListener {

    private lateinit var methodHack: MethodHack

    private lateinit var field: T

    override fun getValue(
        thisRef: Any,
        property: KProperty<*>) = field

    override fun setValue(
        thisRef: Any,
        property: KProperty<*>,
        value: T) {
        println("setValue")
        if (::field.isInitialized && field != value) {
            field = value
            initMethod(property.name, value::class)
            notifyChange()
        } else {
            field = value
        }
    }

    private fun notifyChange()
            = belonging.view?.let { methodHack.call(it, field) }

    private fun initMethod(
        propertyName: String, argType: KClass<*>) {
        if (!::methodHack.isInitialized) {
            methodHack = MethodHack("set${propertyName.capitalize()}", listOf(argType))
        }
    }

    override fun onViewAttached() {
        if (::field.isInitialized) notifyChange()
        else throw IllegalStateException(
            "The property is not initialized.")
    }
}