package com.hirosawasawahiko.py

import org.junit.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

class LivePropertyDelegateTest {

    open class Base {

        companion object {

            fun f() {}
        }

        fun setX(x: Int) {
            println("setX(Int)")
        }

        open fun setX(x: String) {
            println("Base::setX(String)")
        }

        fun setX(x: Float) {
            println("setX(Float)")
        }

        fun setX(x: Base) {
            println("setX(Base)")
        }

        fun setX(x: Pair<Int, String>) {
            println("setX(Int, String)")
        }

        fun setX(x: Int, x2: Float) {
            println("setX(Int, Float)")
        }

        class Child: Base() {

            fun setX(x: Long) {
                println("setX(Long)")
            }

            fun setX(x: Double) {
                println("setX(Double)")
            }

            fun setX(x: Child) {
                println("setX(Child)")
            }

            override fun setX(x: String) {
                println("Child::setX(String)")
            }
        }
    }

    class LivePropTest {

        var f by object : ReadWriteProperty<Any, Int> {

            var field = 0

            override fun getValue(thisRef: Any, property: KProperty<*>): Int {
                println("getValue($field)")
                return field
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
                println("setValue($value)")
                field = value
            }
        }
    }

    @Test
    fun liveDelegagte() {
        val p = LivePropTest()
        p.f = 10
        p
    }

    @Test
    fun test() {
        val child = Base.Child()
        val klass = child::class
        val value = "a" to "a"

        val findSetter: (func: KFunction<*>) -> Boolean = { f ->
            if (f.name != "setX") false
            else if (f.parameters.size < 2) false
            else (f.parameters[1].type.classifier as? KClass<*>)
                ?.isSuperclassOf(value::class)
                ?: false
        }

        val func = klass.memberFunctions.find(findSetter)

        func?.let {
            println("func::${it.name} --------------")
            println("isAccessable = ${it.isAccessible}")
            it.parameters.forEach {  p ->
                println("  param::${p.name}, type=${p.type}")
            }
            println("-------------------------------")

            println("call!")
            it.call(child, value)
        }

        func ?: println("function not found...")

    }

    class X {
        init {
            println("init")
        }
    }

    @Test
    fun tt() {
        var x: X? = null
        for (i in 1..10) {
            println("i=$i")
            x = x ?: X()
        }
    }

    class ObsList<K, V>(
        private val delegate: MutableMap<K, V> = mutableMapOf()
    ): MutableMap<K, V> by delegate {

        override fun clear() {
            println("clear")
            delegate.clear()
        }

        override fun put(key: K, value: V): V? {
            println("put")
            return delegate.put(key, value)
        }

        override fun putAll(from: Map<out K, V>) {
            println("putAll")
            delegate.putAll(from)
        }

        override fun remove(key: K): V? {
            println("remove")
            return delegate.remove(key)
        }
    }

    inline fun <reified T> reifiedFunc() {

    }

    @Test
    fun oblist() {
        val list = ObsList<String, Int>()
        list["a"] = 0
        list.putAll(mapOf("b" to 1, "c" to 2))
        list.remove("a")
        list.clear()
    }

    fun testFunction(q: Int, x: Pair<Int, String>, map: MutableMap<Pair<Boolean, String>, List<Int>>) {
        println("x.first^2 = ${x.first*x.first}")
        println("x.second=${x.second}")
    }

    @Test
    fun typeCheck() {
        ::testFunction.parameters.forEach {
            println("param:${it.name}, type:${it.type}")
        }
    }

    class Target {

        fun f1(x: Int) {
            println("f1($x)")
        }

        fun f1(x: Int, t: Float, s: Pair<Boolean, Int>) {
            println("f1($x, $t, ${s.first}, ${s.second})")
        }

        fun f1(x: Pair<Int, String>) {
            println("f1(${x.first}, ${x.second})")
        }
    }

    @Test
    fun sender() {
        val target = Target()
        val sender1 = MethodHack("f1", listOf(Int::class, Float::class, Pair::class))
        sender1.call(target, 0, 1f, true to 0)
        sender1.call(target, 0, 1f, 0 to true)
    }

    @Test
    fun observableMap() {
        val map = ObservableMap(mutableMapOf(0 to 1, 1 to 2, 2 to 3, 3 to 4, 4 to 5, 5 to 6))
        map[7] = 8
        map.set(0, 0)
        println(map)
        map.entries.removeIf { it.key == 0 }
        println(map)
        map.keys.removeIf { it == 2 }
        println(map)
        map.values.removeIf { it == 5 }
        println(map)
        map.entries.clear()
        print(map)
    }
}