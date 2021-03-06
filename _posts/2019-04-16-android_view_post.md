---
layout: post
title: Android View.post
catalog: false
tags:
    - Android
    - 源码

---

### View.post 使用

1. 确保执行 runnable 的时候，View 已经初始化完成。

2. 可以在将 runnable 丢到 UI 线程去执行

   > Causes the Runnable to be added to the message queue. The runnable will be run on the user interface thread.

3. 一个例子：
  ```kotlin
  thread { 
  	//get image from network
  	imageView.post { 
  		imageView.setImageDrawable(...)
  	}
  }
  ```

4. 使用场景：在 Activity -> onCreate()中我们获取不到view的长宽，但我们这样的话，我们就能得到啦：

   ```kotlin
   override fun onCreate(savedInstanceState: Bundle?) {
   	imageView.post {
   		val imageWidth = imageView.width
   		val imageHeight = imageView.height
   	}
   }
   ```

### 源码浅析

1. post 方法是放在 View.java 中的，意味着所有 View 都能用。

2. post 源码：

   ```kotlin
   /**
    * Causes the Runnable to be added to the message queue.
    * The runnable will be run on the user interface thread.
    */
   public boolean post(Runnable action) {
   		// mAttachInfo 是在 dispatchAttachedToWindow 方法中赋值的
       final AttachInfo attachInfo = mAttachInfo;
       if (attachInfo != null) {
           return attachInfo.mHandler.post(action);
       }
   
       // Postpone the runnable until we know on which thread it needs to run.
       // Assume that the runnable will be successfully placed after attach.
       // 保存到 View 的变量 mRunQueue 中，等待 dispatchAttachedToWindow 中调用
       // executeActions方法，
       getRunQueue().post(action);
       return true;
   }
   
   void dispatchAttachedToWindow(AttachInfo info, int visibility) {
           mAttachInfo = info;
           ...
           if (mRunQueue != null) {
               mRunQueue.executeActions(info.mHandler);
               mRunQueue = null;
           }
   }
   ```

3. 由上述可知，View.post 方法肯定是在 dispatchAttachedToWindow 时以及之后会被调用。
4. 而且，都是通过 mAttachInfo.mHandler 来调用 runnable 的。
5. 所以，一切都在 dispatchAttachedToWindow 的调用，经过查找，可以 确定是在 ViewRootImpl -> performTraversals() 中执行的，所以保证了在调用 runnable 之前已经调用过 measure,layout方法了
6. 详细源码解析可以参考[ View.post()到底干了啥](https://www.jianshu.com/p/85fc4decc947), 以及[View.post() 不靠谱的地方你知道吗？](https://www.jianshu.com/p/5f602fd6cd41)



### 和 runOnUiThread 有啥区别么？

```kotlin
public final void runOnUiThread(Runnable action) {
    if (Thread.currentThread() != mUiThread) {
        mHandler.post(action);
    } else {
        action.run();
    }
}
```

源码很简单，可以知道 runOnUiThread 只是把 runable 丢到 UI线程，然后就马上执行了。没有像 View.post 一样只在 dispatchAttachedToWindow之后才会执行。

### 和MessageQueue.IdleHandler 有啥区别？

1. 使用：

   ```java
   Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
       @Override
       public boolean queueIdle() {
           
           return false;
       }
   });
   ```

2. runnable 会在 MessageQueue 被清空的时候执行，使用场景可以有：

   1. 在 Activity 中等待所以UI显示完，再执行的操作。
   2. 作为一种懒执行的策略，不用在 CPU 高峰期占用资源。

3. 参考自：[你知道android的MessageQueue.IdleHandler吗？](https://mp.weixin.qq.com/s/KpeBqIEYeOzt_frANoGuSg)

