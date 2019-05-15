package com.hirosawasawahiko.tracking

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.hirosawasawahiko.tracking", appContext.packageName)
    }

    class Prop<T>(initial: T): ReadWriteProperty<Any?, T> {

        var field = initial

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            println("getter")
            return field
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            println("setter")
            field = value
        }
    }
}
