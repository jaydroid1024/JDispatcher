package com.jay.android.dispatcher.register.asm

import com.jay.android.dispatcher.common.CommonConst
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author jaydroid
 * @version 1.0
 * @date 7/19/21
 */
class ApplicationOnTerminateMethodVisitor(mv: MethodVisitor) : MethodVisitor(Opcodes.ASM5, mv) {

    fun callSuper() {
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "android/app/Application",
            CommonConst.METHOD_ON_TERMINATE,
            "()V",
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
            );
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/jay/android/dispatcher/launcher/JDispatcher\$Companion",
                "getInstance",
                "()Lcom/jay/android/dispatcher/launcher/JDispatcher;",
                false
            );
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "com/jay/android/dispatcher/launcher/JDispatcher",
                CommonConst.METHOD_ON_TERMINATE,
                "()V",
                false
            );
        }
        super.visitInsn(opcode)
    }


    companion object {
        fun visitMethodOnNotExist(classVisitor: ClassVisitor) {
            val methodVisitor: ApplicationOnTerminateMethodVisitor =
                classVisitor.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    CommonConst.METHOD_ON_TERMINATE,
                    "()V",
                    null,
                    null
                ) as ApplicationOnTerminateMethodVisitor
            methodVisitor.visitCode()
            methodVisitor.callSuper()
            methodVisitor.visitInsn(Opcodes.RETURN)
            methodVisitor.visitEnd()
        }
    }

}