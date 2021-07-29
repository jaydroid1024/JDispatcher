package com.jay.android.dispatcher.register.asm

import com.jay.android.dispatcher.common.CommonUtils
import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.register.utils.PluginConst
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*


open class JDispatcherRegisterMethodVisitor(
    mv: MethodVisitor?,
    private val dispatchSortedList: ArrayList<DispatchItem>
) : MethodVisitor(Opcodes.ASM5, mv) {

    override fun visitCode() {
        super.visitCode()
        val methodVisitor = mv
        dispatchSortedList.forEach { dispatchItem ->
            val dispatchItemJson = CommonUtils.GSON.toJson(dispatchItem)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/jay/android/dispatcher/launcher/JDispatcher",
                "getDispatchSortedJsonListFromPlugin",
                "()Ljava/util/ArrayList;",
                false
            );
            methodVisitor.visitLdcInsn(dispatchItemJson)
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/util/ArrayList",
                "add",
                "(Ljava/lang/Object;)Z",
                false
            );
            methodVisitor.visitInsn(Opcodes.POP)
        }
    }
}