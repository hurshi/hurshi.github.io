---
layout: post
title: kotlin 协程
catalog: false
tags:
    - kotlin
---

### 最简单的使用

```kotlin
GlobalScope.launch(Dispatchers.IO) {
	...
}
```
### 什么是协程
1. 在程序中处理并发任务的方案，也是这种方案的一个组件。
2. 协程和线程属于同一个层级的概念。
   1. 协程中不存在线程，也不存在并行。

### 并发

```kotlin
GlobalScope.launch(Dispatchers.Main) {
    val result1 = async { ... }
    val result2 = async { ... }
    textView.text = "${result1.await()} - ${result2.await()}"
}
```

### 协程泄漏

和新开线程类似，在 Activity  销毁的时候，正在执行的协程不会停止而导致异常问题。

1. 手动取消某个协程

   ```kotlin
   val job = GlobalScope.launch {  }
   job.cancel()
   ```

2. 通过 Scope 管理协程

   ```kotlin
   val scope = MainScope()
   scope.launch { ... }
   scope.launch { ... }
   scope.cancel() // 取消所有使用 scope 的协程
   ```

### 在 Architecture components 中使用协程

1. ViewModelScope

2. LifecycleScope

   ```kotlin
   lifecycleScope.launch { ... }
   lifecycleScope.launchWhenCreated { ... }
   lifecycleScope.launchWhenStarted { ... }
   lifecycleScope.launchWhenResumed { ... }
   ```

   并且不用在 onDestory  中执行 `lifecycleScope.cancel()`，因为这会被自动执行。

### 其他

* 关于**并发**和**并行**：[来源知乎](https://www.zhihu.com/question/33515481)
  1. 你吃饭吃到一半，电话来了，你一直到吃完了以后才去接，这就说明你不支持并发也不支持并行。
	2. 你吃饭吃到一半，电话来了，你停了下来接了电话，接完后继续吃饭，这说明你支持并发。
  3. 你吃饭吃到一半，电话来了，你一边打电话一边吃饭，这说明你支持并行。
* 事件流 Flow