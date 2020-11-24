---
layout: post
title: Android Q-A
subtitle: 自问自答
catalog: true
tags:
    - Android
---

### 可以在 Service 中启动 Activity 么？

可以的

1. 需要   `addFlag( FLAG_ACTIVITY_NEW_TASK)`.

2. 弊端：会在系统的“最近任务”中显示，可以通过 `android:excludeFromRecents="true"` 解决

3. 兼容：在 oppo, vivo 手机中，默认不能跳转，需要引导用户开启“自启动”权限才可以。

### 可以在 Service 中弹出 Dialog 么？

可以的

```java
// 1. 需要权限
android.permission.SYSTEM_ALERT_WINDOW
// 2. 设置 window type
dialog.getWindow().setType(WindowManager.LayoutParams.TPYE_SYSTEM_ALERT)
```

### 如何实现锁屏呢？

1. 监听灭屏广播事件，在灭屏的时候启动锁屏 Activity。

1. 在锁屏 Activity 中尝试屏蔽系统锁屏。如果屏蔽不了，要通过 setShowWhenLocked(true) 等方法来使的锁屏 Activity 在锁屏界面的最上层。

   ```java
   getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD 
                  | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
   if (android.os.Build.VERSION.SDK_INT >= 26) {
   	((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE))
     		.requestDismissKeyguard(this, null);
   }
   if (android.os.Build.VERSION.SDK_INT >= 27) {
   	setShowWhenLocked(true);
   }
   ```


4. 兼容：在 小米，魅族手机上，需要引导用户开启“锁屏”权限才能在锁屏界面显示。

### Retrofit 源码相关：

1. 缓存：对每个进来的 Method 都会创建一个 ServiceMethod 实例，并保存在 ConcurrentHashMap 中。 
2. ServiceMethod 负责的内容：解析注解，配置 CallAdapter，配置 Converter。
  1. 解析注解：解析调用方法，参数的注解。
  2. 配置 CallAdapter：是 retrofit Call 的转换器，比如将 OkhttpCall 转换为 RxJava。
  3. 配置 Converter：对返回数据进行转换，比如 Gson 序列化/反序列化，可以配置多个 converter, retrofit 会依次调用这些 converters, 遇到能转换的 converter 就直接返回，不再往下掉用。

### OkHttp

1. 责任链模式：OKhttp 通过责任链模式来解耦不同模块。

### AAC - LifeCycle

1. LifeCycle 是如何感应生命周期的？

   > Activity 的父类 SupportAcitivty 中注入了 ReportFragment，在 ReportFragment 监听生命周期的变化，然后反馈给 SupportActivity 的变量 mLifecycleRegistry，在 mLifecycleRegistry 中调用 observer 对应的生命周期方法。

2. 是如何调用到有 `@OnLifecycleEvent(Lifecycle.Event.ON_START)` 注解的生命周期方法的？

   > 在 addObserver 的时候，会对 observer 进行解析，如果是上面这种注解的方式，则会为 observer 包装为 ReflectiveGenericLifecycleObserver，通过反射的方式调用。

3. 参考：[【AAC 系列二】深入理解架构组件的基石：Lifecycle](https://github.com/AlanCheen/FullStackNotes/blob/master/android/aac-lifecycle.md)


### AAC - ViewModel

1. ViewModel 是如何在旋转屏幕的时候保存数据的？

   > 通过注入一个 **setRetainInstance(true)*### 的 HolderFragment，保存 ViewModel 的 ViewModelStore 实例在这个 HolderFragment 中，以此实现了 ViewModel 的生命周期。

2. ViewModel 的实例：

   1. 通过 ViewModelProvider 获得实例；

3. 参考：[【AAC 系列四】深入理解架构组件：ViewModel](https://github.com/AlanCheen/FullStackNotes/blob/master/android/aac-viewmodel.md)

### AAC - LiveData

1. LiveData 是如何和 LifeCycle 结合感知生命周期的？

   > 在 LiveData 的 observe 时候，会传入 LifecycleOwner，在每次 onStateChanged 的时候，会判断是否 DESTORYED，如果已经 DESTORYED 就 remove 掉不再 activeStateChanged。
   
   ```java
   @Override
   boolean shouldBeActive() {
   	return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
   }
   
   @Override
   public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
   	if (mOwner.getLifecycle().getCurrentState() == DESTROYED) {
    		removeObserver(mObserver);
    		return;
   	}
   	activeStateChanged(shouldBeActive());
   }
   ```

2. 参考：[【AAC 系列三】深入理解架构组件：LiveData](https://github.com/AlanCheen/FullStackNotes/blob/master/android/aac-livedata.md)

### 讲讲 Handler 原理

1. 当新线程发送一条 message 到 handler  中时，会把这条 message 保存进 messageQueue 中；

2. 在 handler 线程中，会有一个永动机一样的 Looper，不断从 messageQueue 中读取新数据，并在本线程处理这个 message。

3. Looper 是保存在 ThreadLocal 中的，保证这个 Looper 本线程独享。

4. 为什么 Handler 不卡线程？

   > 其实是在进入 Looper.loop() 这个死循环之前，就开启了新的 binder 线程，这个 binder 线程用于接收系统服务 AMS 发来的事件。

5. Activity 的生命周期是如何在死循环中执行起来的？

   > binder 线程会从 AMS 收到生命周期事件，然后发送给主线程。主线程收到消息后根据不同的msg，执行相应的生命周期。

6. 参考：[Android中为什么主线程不会因为Looper.loop()里的死循环卡死？](https://www.zhihu.com/question/34652589/answer/90344494)

### 为什么用Binder

1. IPC 机制有：管道，消息队列，共享内存，Socket等。
2. 除了共享内存是0次拷贝数据，其他的性能都低于 Binder。
3. 相比于 共享内存，Binder 的有点：
   1. Binder 安全性更高。
   2. Binder 是 C/S 架构，职责明确，架构清晰，稳定性高。
4. 参考：[Android Binder原理（一）学习Binder前必须要了解的知识点](http://liuwangshu.cn/framework/binder/1-intro.html)

### SharedPreference 导致 ANR 的原因
1. SharedPreference 的 apply 方法，目的在于将数据持久化到本地；虽然持久化数据的操作在异步线程，但它会创建等待锁放到 QueuedWork 中，等持久化到本地执行完毕才会释放；
2. 而 `Activity#onStop`,`Service#onStop` 等方法都会调用 `QueuedWork.waitToFinish` 方法，导致阻塞，从而导致 ANR；
3. 猜测 SharedPreference 的意图，应该在于保证本地持久化数据的完整性以及可靠性，所以使用了 QueuedWork；
4. 对 SharedPreference  的使用，普遍存在滥用：
   1. 仅保存轻量数据，因为 SharedPreference 会将所有数据读进缓存中去，如果是大量数据会很耗内存；
   2. SharedPreference 中的  commit/apply 方法不能滥用，当需要多次写入时候，尽量复用 commit/apply，而不是每提交一次就 commit/apply 一次；
5. 参考：[字节跳动 - 剖析 SharedPreference apply 引起的 ANR 问题](https://mp.weixin.qq.com/s/IFgXvPdiEYDs5cDriApkxQ)

##### 半成品

1. view 的绘制
2. 事件分发
3. 几种锁，死锁怎么办，如何设置超时时长
4. 多线程
5. recycleView  https://github.com/AlanCheen/Flap
6. mvp vs mvvm
7. retrofit 源码，缓存
8. okhttp 线程池
9. 模块化开发如何快速独立编译开发
10. 注解如何支持增量编译的
11. Arouter startActivity vs Intent
12. recycleview 分割线，图片回收，拖拽
13. kotlin 派生对象，单利，空指针，可见性，inline函数
14. http header等
15. java 高可用线程安全缓存，线程挂起，
16. Glide 缓存
17. Mediaplayer 的状态，Mediaplayer 的 IlligleException
18. Android MultiDex 的优化
19. binder
21. Arouter 源码
22. JessYan 的一些文章：https://url.cn/5RdH9ti
23. 动态代理 和 反射

##### 一些文章

* Java 技术
  * [HTTP 必知必会的那些](https://mp.weixin.qq.com/s/Fazx13maQfPJItfkOqk9FQ)
  * [LruCache 源码解析](https://mp.weixin.qq.com/s/CUxg8JAOR_YNK-bHX_LyAg)
  * [从ConcurrentHashMap的演进看Java多线程核心技术](http://www.jasongj.com/java/concurrenthashmap/)

* Android Framework
	* [Android进阶——Android四大组件启动机制之Activity启动过程](https://blog.csdn.net/qq_30379689/article/details/79611217)
	* [Android四大组件之Activity--启动过程(上)](https://duanqz.github.io/2016-07-29-Activity-LaunchProcess-Part1)
	* [Android 7.0 ActivityManagerService(1) AMS的启动过程](https://blog.csdn.net/gaugamela/article/details/53067769)
	* [Android视图绘制流程完全解析，带你一步步深入了解View(二)](https://blog.csdn.net/guolin_blog/article/details/16330267)
	* [Android Framework 源码分析](https://glumes.com/tags/framework/)
	* [Android进阶之旅](https://www.jianshu.com/nb/8948928)
	* [Android 系统服务 - AMS 的启动过程](https://www.jianshu.com/p/0c1b2ffa5842)
	* [Android Framework启动流程浅析](https://juejin.im/post/5dc00368f265da4d1d32f911)
	* [写给 Android 应用工程师的 Binder 原理剖析](https://zhuanlan.zhihu.com/p/35519585)
	* [Activity、View、Window的理解一篇文章就够了](https://www.jianshu.com/p/5297e307a688)
	* [Android View的绘制流程](https://jsonchao.github.io/2018/10/28/Android%20View%E7%9A%84%E7%BB%98%E5%88%B6%E6%B5%81%E7%A8%8B/)
	* [Android事件分发机制 详解攻略，您值得拥有](https://blog.csdn.net/carson_ho/article/details/54136311)
	* [Android触摸事件传递机制](https://jsonchao.github.io/2018/10/17/Android触摸事件传递机制/)
	* [ViewRootImpl 和 DecorView 分析](https://www.jianshu.com/p/90173fc5745b)
	* [Android 绘制原理浅析【干货】](https://juejin.im/post/5d4176365188255d8919be91)
	* [Android 子线程更新UI了解吗？](https://juejin.im/post/5da14e8ae51d45782b0c1c20)
	* [Android Context 的设计思想](https://mp.weixin.qq.com/s/EsonF2tDYvHcCM3Z6drujw?)
	* [Android跨进程通信：图文详解 Binder机制 原理](https://blog.csdn.net/carson_ho/article/details/73560642)
	* [Android中Bitmap内存优化](https://www.jianshu.com/p/3f6f6e4f1c88)
	* [Android 性能优化最佳实践](https://juejin.im/post/5b50b017f265da0f7b2f649c)
  * [Android性能优化典范 - 第6季](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=2653578016&idx=1&sn=d997d1142bac09e3764c075392468ae5&chksm=84b3b127b3c4383197c7d1cf15ecec44d66a1119b033ae383f9e2126bb1be0abc93416622dc0&scene=21#wechat_redirect)
  * [Android 高级进阶之路](https://github.com/AlexTiti/AndroidAdvanceRoute)
  * [Android 源码 Alex@W](https://me.csdn.net/Alexwll)
  * [Android 内存优化总结&实践](https://mp.weixin.qq.com/s/2MsEAR9pQfMr1Sfs7cPdWQ)
  * [JavaGuide](https://github.com/Snailclimb/JavaGuide)
  * [https://duanqz.github.io/](https://duanqz.github.io/)
* 面试题
	* [Android篇：2019初中级Android开发社招面试解答（上）](https://juejin.im/post/5c8211fee51d453a136e36b0#heading-24)
  * [面试官又来了：你的app卡顿过吗？](https://juejin.im/post/5d837cd1e51d4561cb5ddf66#heading-2)
  * [Android 面试题集合（2018）](https://juejin.im/post/5b6b9d6f5188253c603c87c6)
  * [渣渣二本的辛酸面试之路](https://juejin.im/post/5caf0f89f265da038145c66e)
  * [JsonChao 面试](https://mubu.com/doc/uRmziI6te0)
  * [Android高级面试题 ](https://github.com/JsonChao/Awesome-Android-Interview/blob/master/Android相关/Android高级面试题.md)
  * [Android 面试经验 - 大厂 OPPO 面](https://www.jianshu.com/p/ffb1cc684507)
  * [Android 面试经验 - 大厂 腾讯 面](https://www.jianshu.com/p/80971ed644a2?utm_campaign=haruki&utm_content=note&utm_medium=reader_share&utm_source=qq)
  * [Android 相关知识点个人总结](https://juejin.im/post/5db929a66fb9a0202a2624fe)
  * [Android 高级开发面试题以及答案整理](https://juejin.im/post/5c8b1bd56fb9a049e12b1692) 