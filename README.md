# JDispatcher
Android 组件生命周期分发框架，适用于组件化，模块化，启动优化等场景

## 最新版本

[![](https://jitpack.io/v/jaydroid1024/JDispatcher.svg)](https://jitpack.io/#jaydroid1024/JDispatcher)

```groovy
//Step 1. Add the JitPack repository to your build file
//buildscript & allprojects
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
//dependencies
classpath 'com.github.jaydroid1024.JDispatcher:jdispatcher-plugin:1.0.0'

//Step 2. Add the dependency
implementation 'com.github.jaydroid1024.JDispatcher:jdispatcher-api:1.0.0'
kapt 'com.github.jaydroid1024.JDispatcher:jdispatcher-compiler:1.0.0'

//Step 3. apply the plugin and config dispatcher
apply plugin: 'jdispatcher'
dispatcher {
    appCanonicalName = "com.jay.android.App"
    buildIncremental = true
    buildDebug = true
}

```



## 功能介绍

- 实现 Application 各个生命周期方法在所有需要的组件中分发
- 分发顺序支持多种规则
  - 优先级：优先级值越大越先被调用
  - 依赖项：组件依赖的分发类先初始化
- 分发维度支持多种规则
  - 在指定进程(所有进程，主进程，非主进程)中分发
  - 在指定线程(主线程，空闲线程，工作线程)中分发，实现异步加载
  - 手动延迟调用分发，实现延迟加载
  - 只在debug模式下分发，实现 DevTools、DoKit 等开发工具的初始化
- 维度值采用对整型 or/and 的位操作完成多维度值的收集与识别，灵活且高效
- 支持初始化时批量传参，可用于多项目多环境的三方 sdk 的初始化，环境配置更统一
- 通过注解打点，APT 收集分发类，低耦合，可用于模块化，组件化场景
- 通过拦截 AGP 构建流程实现在编译期间扫描和排序，提高运行时性能
- 通过 ASM 字节码插桩实现分发表和 Application  生命周期方法的自动注入，集成更高效
- 支持统计所有分发类的初始化时间，可用于启动优化排查



## 框架结构

- jdispatcher-annotation
  - 模块类型：apply plugin: 'java'
  - 模块描述：声明编译时所需的注解类以及公共类等

- jdispatcher-compiler
  - 模块类型：apply plugin: 'java'
  - 模块描述：编译期(.java--.class阶段) 收集和处理整个工程中的Dispatch注解信息并通过 javapoet 生成辅助类文件 JDispatcher$$Group_hash.java 

- jdispatcher-plugin

  - 模块类型：apply plugin: 'groovy'

  - 模块描述：编译期(.class--.dex阶段) 自定义Transform拦截AGP的构建过程，找到所有Dispatch并排序

  - 模块主要工作：
    - 第一个：IDispatch 的分发流程
      - 扫描到所有 APT 生成的 JDispatcher$$Group_hash.java 文件
      - 反射获取收集到的 Map<String, DispatchItem> atlas)
      - 通过 atlas 集合收集到的 DispatchItem 实现对 IDispatch 对象的反射实例化
      - 按照 DispatchItem 的排序规则完成排序操作
      - 将排好序的 IDispatch 集合通过字节码插桩到 JDispatcher 中，运行时执行对所有 IDispatch 的分发操作
    - 第二个：Application 生命周期方法的自动注册流程
      - 通过调用方在gradle中配置的 Application 全类名，在自定义Transform中扫描到该类
      - JDispatcher 调用字节码注入到 onTerminate()
      - JDispatcher 调用字节码注入到 onConfigurationChanged(newConfig: Configuration)
      - JDispatcher 调用字节码注入到 onLowMemory()
      - JDispatcher 调用字节码注入到 onTrimMemory(level: Int)
- jdispatcher-api
  - 模块类型：apply plugin: 'com.android.library'
  - 模块描述：运行时用于整个框架的初始化，运行时分发等操作



## 使用说明



```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化并自动分发
        JDispatcher.instance
            .withDebugAble(true)
            .init(this)
    }
}
```



```kotlin
//手动调用分发
JDispatcher.instance
    .manualDispatch("com.jay.android.jdispatcher.DispatcherAppDemo")
```



```kotlin
//声明分发类

@Dispatch(priority = Priority.LOW_DEFAULT, description = "DispatcherAppDemo")
public class DispatcherAppDemo extends DispatchTemplate {

    @Override
    public void onCreate(@NotNull Application app, @NotNull DispatchItem dispatchItem) {
        Log.d("Jay", "DispatcherAppDemo#onCreate" + dispatchItem);
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        Log.d("Jay", "DispatcherAppDemo#onConfigurationChanged" + newConfig);
    }

    @Override
    public void onLowMemory() {
        Log.d("Jay", "DispatcherAppDemo#onLowMemory");

    }

    @Override
    public void onTerminate() {
        Log.d("Jay", "DispatcherAppDemo#onTerminate");

    }

    @Override
    public void onTrimMemory(int level) {
        Log.d("Jay", "DispatcherAppDemo#onTrimMemory" + level);

    }
}
```

