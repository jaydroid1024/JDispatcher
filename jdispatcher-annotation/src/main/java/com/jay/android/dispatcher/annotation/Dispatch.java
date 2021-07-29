package com.jay.android.dispatcher.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要分发 Application life 的注解类
 *
 * @author jaydroid
 * @version 1.0
 * @date 5/31/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Dispatch {

    /**
     * 当前 App Dispatcher 的唯一识别信息，缺省时为全类名
     *
     * @return id
     */
    String name() default "";

    /**
     * 初始化优先级，值越大越先初始化。
     * 默认为中等优先级，详见：{@link Priority}，也可自行定义优先级策略
     *
     * @return priority
     */
    int priority() default Priority.MEDIUM_DEFAULT;

    /**
     * 在指定依赖项之后初始化，用于整个项目范围内重新排序
     *
     * @return dependencies
     */
    String[] dependencies() default {};

    /**
     * Dispatch 分发维度数据，可由用户设置。
     * 你应该使用整数 num 来标记类型，通过位运算来标记，例如 10001010101010
     * 详见: {@link Dimension}
     * - 进程维度类型: {@link Dimension#PROCESS_ALL}、{@link Dimension#PROCESS_MAIN}、{@link Dimension#PROCESS_OTHER}
     * - 线程维度类型: {@link Dimension#THREAD_UI}、{@link Dimension#THREAD_UI_IDLE}、{@link Dimension#THREAD_WORK}
     * - 调试维度类型: {@link Dimension#BUILD_DEBUG}、{@link Dimension#BUILD_ALL}
     * - 初始化时机维度类型: {@link Dimension#AUTOMATIC}、{@link Dimension#MANUAL}
     *
     * @return dimension
     */
    int dimension() default Dimension.DIMENSION_DEFAULT;

    /**
     * 当前 App Dispatcher 的描述信息
     *
     * @return description
     */
    String description() default "";


}
