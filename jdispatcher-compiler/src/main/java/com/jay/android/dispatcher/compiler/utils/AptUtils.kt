package com.jay.android.dispatcher.compiler.utils

import com.jay.android.dispatcher.compiler.messager.Logger
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/23/21
 */
object AptUtils {

    object AptContext {

        var isInitFinish: Boolean = false
        lateinit var types: Types
        lateinit var elements: Elements
        lateinit var msg: Messager
        lateinit var filer: Filer
        lateinit var options: Map<String, String>

        @JvmStatic
        fun init(processingEnv: ProcessingEnvironment) {
            init(processingEnv, true)
        }

        @JvmStatic
        fun init(processingEnv: ProcessingEnvironment, idDebug: Boolean) {
            Logger.isDebug = idDebug
            elements = processingEnv.elementUtils
            types = processingEnv.typeUtils
            msg = processingEnv.messager
            filer = processingEnv.filer
            options = processingEnv.options
            isInitFinish = true
        }

    }

    @JvmStatic
    fun getTypeName(canonicalName: String?): TypeName? {
        return ClassName.get(AptContext.elements.getTypeElement(canonicalName))
    }

    @JvmStatic
    fun getHash(element: Element?): String {
        if (element is TypeElement) {
            val hash = hash(element.qualifiedName.toString())
            if (hash.isNotEmpty()) {
                return hash
            }
        }
        return hash(UUID.randomUUID().toString())
    }

    @JvmStatic
    fun hash(str: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            md.update(str.toByteArray())
            BigInteger(1, md.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            Integer.toHexString(str.hashCode())
        }
    }
}