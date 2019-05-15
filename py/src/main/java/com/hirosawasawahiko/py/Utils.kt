package com.hirosawasawahiko.py

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Utils {

    fun <R, T> notnull(initial: T?,
                       placeholder: (thisRef: R) -> T) =

        object: ReadWriteProperty<R, T> {

            private var field: T? = initial

            override fun getValue(
                thisRef: R,
                property: KProperty<*>)
                    = field ?: placeholder(thisRef)

            override fun setValue(
                thisRef: R,
                property: KProperty<*>,
                value: T) {
                field = value
            }
        }

    fun irreversible(initial: Boolean = false) =

        object: ReadWriteProperty<Any?, Boolean> {

            private var inversed = false

            override fun getValue(
                thisRef: Any?,
                property: KProperty<*>)
                    = if (inversed) !initial else initial

            override operator fun setValue(
                thisRef: Any?,
                property: KProperty<*>,
                value: Boolean) {
                if (value != initial) inversed = true
            }
        }

    fun <T> readableUntil(value: T,
                          condition: () -> Boolean,
                          otherwise: () -> Nothing) =

            object: ReadWriteProperty<Any?, T> {

                override fun getValue(
                    thisRef: Any?,
                    property: KProperty<*>) =
                    if (condition()) value else otherwise()

                override fun setValue(
                    thisRef: Any?,
                    property: KProperty<*>, value: T) {
                    // Do nothing here.
                }
            }
}