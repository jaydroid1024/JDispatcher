package com.jay.android.dispatcher.register.asm

import com.jay.android.dispatcher.common.DispatchItem
import com.jay.android.dispatcher.common.Logger
import com.jay.android.dispatcher.register.utils.PluginConst.METHOD_OF_JDISPATCHER_TO_INJECT
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*


open class JDispatcherClassVisitor(
    classVisitor: ClassVisitor,
    private val dispatchSortedList: ArrayList<DispatchItem>
) : ClassVisitor(Opcodes.ASM5, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
//        Logger.debug(
//            "JDispatcherClassVisitor : visitMethod -----> started:"
//                    + "-access:" + access
//                    + "-name:" + name
//                    + "-descriptor:" + descriptor
//                    + "-signature:" + signature
//                    + "-exceptions:" + exceptions
//        )
        var mv = cv.visitMethod(access, name, descriptor, signature, exceptions)
        if (METHOD_OF_JDISPATCHER_TO_INJECT == name) {
            Logger.debug("visit method name is $name")
            mv = JDispatcherRegisterMethodVisitor(mv, dispatchSortedList)
        }
        return mv
    }
}