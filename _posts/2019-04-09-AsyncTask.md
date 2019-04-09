---
layout: post
title: AsyncTask 源码逻辑
catalog: false
tags:
    - Android
    - 源码
---

1. 先看看一段伪代码，以便快速了解 AsyncTask 的架构。

   ```
   class MyAsyncTask {
       private val callable = object : Callable {
           override fun call(): RESULT {
               doInBackground()
           }
       }
   
       private val runnable = object : RunnableFuture {
           override fun run() {
               callable.call()
           }
       }
   
       private val executor = object : Executor {
           override fun execute(r: Runnable) {
               r.run()
           }
       }
   
       abstract fun doInBackground()
   
       fun execute() {//执行入口
           executor.execute(runnable)
       }
   }
   ```

2. AsyncTask 的主要逻辑就是上面这些，其他的就是细节了。看完这里的逻辑，再去看源码的话就很容易理解了。

3. 进一步深入：下一步可以看看 [正确理解 AsyncTask，Looper,Handler三者之间的关系](http://www.cnblogs.com/punkisnotdead/p/4469612.html)，或者直接看看源码。