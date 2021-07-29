package com.jay.android.dispatcher.compiler.processor

import com.jay.android.dispatcher.compiler.utils.AptUtils
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element

/**
 * Base Processor
 *
 * @author jaydroid
 * @version 1.0
 * @date 6/14/21
 */
abstract class BaseProcessor : AbstractProcessor() {

    private var annotationClass: Class<out Annotation?>? = null

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        AptUtils.AptContext.init(processingEnv)
    }

    fun setElementsAnnotated(annotation: Class<out Annotation?>?) {
        this.annotationClass = annotation
    }

    /**
     * 获取所有被 annotationClass 注解的类
     *
     * @param environment RoundEnvironment
     * @return dispatchElements
     */
    open fun getAnnotationElements(environment: RoundEnvironment): Set<Element?>? {
        return environment.getElementsAnnotatedWith(annotationClass)
    }

    override fun getSupportedAnnotationTypes(): Set<String?>? {
        return setOf(annotationClass?.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }


    override fun getSupportedOptions(): Set<String?>? {
        val optionsSet = hashSetOf<String>()
        return getOptions(optionsSet)
    }

    protected open fun getOptions(optionsSet: HashSet<String>): HashSet<String> {
        return optionsSet
    }


}