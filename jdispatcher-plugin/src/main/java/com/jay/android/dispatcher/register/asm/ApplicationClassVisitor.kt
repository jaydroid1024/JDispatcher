package com.jay.android.dispatcher.register.asm

import com.jay.android.dispatcher.common.CommonConst
import com.jay.android.dispatcher.common.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


open class ApplicationClassVisitor(
    classVisitor: ClassVisitor
) : ClassVisitor(Opcodes.ASM5, classVisitor) {

    private var isHasTerminateMethod = false

    private var isHasConfigurationChangedMethod = false

    private var isHasLowMemoryMethod = false

    private var isHasTrimMemoryMethod = false


    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        Logger.debug(
            "ApplicationClassVisitor : visitMethod -----> started:"
                    + "-access:" + access
                    + "-name:" + name
                    + "-signature:" + signature
                    + "-exceptions:" + exceptions
        )
        var mv = cv.visitMethod(access, name, descriptor, signature, exceptions)

        if (name == CommonConst.METHOD_ON_TERMINATE) {
            mv = ApplicationOnTerminateMethodVisitor(mv)
            isHasTerminateMethod = true
            Logger.debug("visit method name is $name")
        }

        if (name == CommonConst.METHOD_ON_CONFIGURATION_CHANGED) {
            mv = ApplicationOnConfigurationChangedMethodVisitor(mv)
            isHasConfigurationChangedMethod = true
            Logger.debug("visit method name is $name")
        }

        if (name == CommonConst.METHOD_ON_LOW_MEMORY) {
            mv = ApplicationOnLowMemoryMethodVisitor(mv)
            isHasLowMemoryMethod = true
            Logger.debug("visit method name is $name")
        }
        if (name == CommonConst.METHOD_ON_TRIM_MEMORY) {
            mv = ApplicationOnTrimMemoryMethodVisitor(mv)
            isHasTrimMemoryMethod = true
            Logger.debug("visit method name is $name")
        }
        return mv

    }

    override fun visitEnd() {
        Logger.debug(
            "ApplicationClassVisitor : visitEnd ----->:" +
                    "isHasTerminateMethod= $isHasTerminateMethod, " +
                    "isHasConfigurationChangedMethod= $isHasConfigurationChangedMethod,  " +
                    "isHasLowMemoryMethod= $isHasLowMemoryMethod,  " +
                    "isHasTrimMemoryMethod= $isHasTrimMemoryMethod, "
        )

        if (!isHasTerminateMethod) {
            ApplicationOnTerminateMethodVisitor.visitMethodOnNotExist(this)
        }
        if (!isHasConfigurationChangedMethod) {
            ApplicationOnConfigurationChangedMethodVisitor.visitMethodOnNotExist(this)
        }
        if (!isHasLowMemoryMethod) {
            ApplicationOnLowMemoryMethodVisitor.visitMethodOnNotExist(this)
        }
        if (!isHasTrimMemoryMethod) {
            ApplicationOnTrimMemoryMethodVisitor.visitMethodOnNotExist(this)
        }
        super.visitEnd()
    }

}