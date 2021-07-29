package com.jay.android.dispatcher.register.asm

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.InputStream

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/21/21
 */
object ASMHelper {

    fun modifyClass(
        inputStream: InputStream,
        classVisitor: (ClassWriter) -> ClassVisitor
    ): ByteArray? {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        classReader.accept(classVisitor.invoke(classWriter), ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}