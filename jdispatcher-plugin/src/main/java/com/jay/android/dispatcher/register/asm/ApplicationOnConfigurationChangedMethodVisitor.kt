package com.jay.android.dispatcher.register.asm

import com.jay.android.dispatcher.common.CommonConst
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *
 * @author jaydroid
 * @version 1.0
 * @date 7/19/21
 */
class ApplicationOnConfigurationChangedMethodVisitor(mv: MethodVisitor) :
    MethodVisitor(Opcodes.ASM5, mv) {

    fun callSuper() {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "android/app/Application",
            CommonConst.METHOD_ON_CONFIGURATION_CHANGED,
            "(Landroid/content/res/Configuration;)V",
            false
        )

    }

    override fun visitInsn(opcode: Int) {
        if (opcode == Opcodes.RETURN) {
            mv.visitFieldInsn(
                Opcodes.GETSTATIC,
                "com/jay/android/dispatcher/launcher/JDispatcher",
                "Companion",
                "Lcom/jay/android/dispatcher/launcher/JDispatcher\$Companion;"
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/jay/android/dispatcher/launcher/JDispatcher\$Companion",
                "getInstance",
                "()Lcom/jay/android/dispatcher/launcher/JDispatcher;",
                false
            )
            mv.visitVarInsn(Opcodes.ILOAD, 1)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/jay/android/dispatcher/launcher/JDispatcher",
                CommonConst.METHOD_ON_CONFIGURATION_CHANGED,
                "(Landroid/content/res/Configuration;)V",
                false
            )
        }
        super.visitInsn(opcode)
    }


    companion object {


        fun visitMethodOnNotExist(classVisitor: ClassVisitor) {
            val methodVisitor = classVisitor.visitMethod(
                Opcodes.ACC_PUBLIC,
                CommonConst.METHOD_ON_CONFIGURATION_CHANGED,
                "(Landroid/content/res/Configuration;)V",
                null,
                null
            ) as? ApplicationOnConfigurationChangedMethodVisitor
            methodVisitor?.visitCode()
            methodVisitor?.callSuper()
            methodVisitor?.visitInsn(Opcodes.RETURN)
            methodVisitor?.visitEnd()
        }
    }
}