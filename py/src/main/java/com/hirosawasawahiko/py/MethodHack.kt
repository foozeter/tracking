package com.hirosawasawahiko.py

//import android.util.Log
import java.lang.ref.SoftReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberFunctions

class MethodHack(
    private val methodName: String,
    private val argTypes: List<KClass<*>>) {
    private var method: KFunction<*>? = null
    private lateinit var receiverType: KClass<*>

    fun call(receiver: Any, vararg args: Any) {
        if (method == null ||
            receiverType != receiver::class) {
            method = obtainMethod(receiver)
            receiverType = receiver::class
        }

        method?.let {
            try {
                it.call(receiver, *args)
            } catch (e: Exception) {
                println("Could't invoke a method '$it' because of generic types mismatch.")
            }
        } ?: println("method was not found...")

        // TODO; log an error message when the method is not found.
//        method ?: Log.e("MethodHack", "A method " +
//                "'$methodName(${argTypes.map { it.simpleName }.joinToString(",")})' " +
//                "is not defined in ${receiverType.simpleName}.")

    }

    private fun obtainMethod(receiver: Any): KFunction<*>? {
        val tag = Tag(methodName, argTypes, receiver::class)
        cachedMethods[tag]?.get()?.let {
            return it
        } ?: receiver::class.memberFunctions.find {
            it.name == methodName &&
                    matchParameterTypes(it.parameters)
        }?.let {
            cachedMethods[tag] = SoftReference(it)
            return it
        } ?: return null
    }

    private fun matchParameterTypes(params: List<KParameter>): Boolean {
        if (params.size - 1 != argTypes.size) return false
        params.forEachIndexed { i, p ->
            if (0 < i && !p.match(argTypes[i-1])) return false
        }
        return true
    }

    private fun KParameter.match(klass: KClass<*>)
            = (type.classifier as? KClass<*>)?.isSuperclassOf(klass) ?: false

    private data class Tag(
        val name: String,
        val args: List<KClass<*>>,
        val belonging: KClass<*>)

    companion object {
        private val cachedMethods = mutableMapOf<Tag, SoftReference<KFunction<*>>>()
    }
}