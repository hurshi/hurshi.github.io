---
layout: post
title: Android Handler跨进程 以及 内存泄漏分析
catalog: true
tags:
    - Android
---


在理解Handler跨线程之前，需要先理解下面几个概念：ThreadLocal , MessageQueue , Looper

##### ThreadLocal

* 一般来说，变量可以被不同线程读写，但是ThreadLocal只有当前线程可以读写，就像多进程一样，不同线程之间访问同一个ThreadLocal变量是完全独立互不干扰的；

* 源码
    ``` java
    public class Thread implements Runnable {
        ...
        ThreadLocal.ThreadLocalMap threadLocals = null;
        ...
    }

    public class ThreadLocal<T> {
        public void set(T value) {
            Thread t = Thread.currentThread();
            ThreadLocalMap map = getMap(t);
            if (map != null)
                map.set(this, value);
            else
                createMap(t, value);
        }

        public T get() {
            Thread t = Thread.currentThread();
            ThreadLocalMap map = getMap(t);
            if (map != null) {
                ThreadLocalMap.Entry e = map.getEntry(this);
                if (e != null)
                    return (T) e.value;
            }
            return setInitialValue();
        }

        ThreadLocalMap getMap(Thread t) {
            return t.threadLocals;
        }
    }
    ```
* 上述内容非常容易理解，每新建一个 Thread 就会在新 Thread 中保留着 ThreadLocalMap 实例，而从 ThreadLocal 中 get value 则会从当前的Thread中去获取 threadLocals 变量，从中取值。以此实现 ThreadLocal 在各自线程中独立保存 value 的功能；

##### MessageQueue

* 源码略长，主要看看有中文注释的地方
     ```java

      public final class MessageQueue {
          boolean enqueueMessage(Message msg, long when) {//插入消息
              if (msg.target == null) {//handler对象
                  throw new IllegalArgumentException("Message must have a target.");
              }
              if (msg.isInUse()) {
                  throw new IllegalStateException(msg + " This message is already in use.");
              }
              synchronized (this) {
                  if (mQuitting) {
                      IllegalStateException e = new IllegalStateException(msg.target + " sending message to a Handler on a dead thread");
                      msg.recycle();
                      return false;
                  }
                  msg.markInUse();
                  msg.when = when;
                  Message p = mMessages;
                  boolean needWake;
                  if (p == null || when == 0 || when < p.when) {
                      // New head, wake up the event queue if blocked.
                      msg.next = p;
                      mMessages = msg;
                      needWake = mBlocked;
                  } else {
                      needWake = mBlocked && p.target == null && msg.isAsynchronous();
                      Message prev;
                      for (; ; ) {
                          prev = p;
                          p = p.next;
                          if (p == null || when < p.when) {
                              break;
                          }
                          if (needWake && p.isAsynchronous()) {
                              needWake = false;
                          }
                      }
                      msg.next = p; // invariant: p == prev.next
                      prev.next = msg;
                  }
                  if (needWake) {
                      nativeWake(mPtr);
                  }
              }
              return true;
          }

          Message next() {//取出消息
              final long ptr = mPtr;
              if (ptr == 0) {
                  return null;
              }
              int pendingIdleHandlerCount = -1; // -1 only during first iteration
              int nextPollTimeoutMillis = 0;
              for (; ; ) {
                  if (nextPollTimeoutMillis != 0) {
                      Binder.flushPendingCommands();
                  }
                  nativePollOnce(ptr, nextPollTimeoutMillis);
                  synchronized (this) {
                      final long now = SystemClock.uptimeMillis();
                      Message prevMsg = null;
                      Message msg = mMessages;
                      if (msg != null && msg.target == null) {
                          do {
                              prevMsg = msg;
                              msg = msg.next;
                          } while (msg != null && !msg.isAsynchronous());
                      }
                      if (msg != null) {
                          if (now < msg.when) {
                              nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                          } else {
                              mBlocked = false;
                              if (prevMsg != null) {
                                  prevMsg.next = msg.next;
                              } else {
                                  mMessages = msg.next;
                              }
                              msg.next = null;
                              msg.markInUse();
                              return msg;
                          }
                      } else {
                          nextPollTimeoutMillis = -1;
                      }
                      if (mQuitting) {
                          dispose();
                          return null;
                      }
                      if (pendingIdleHandlerCount < 0 && (mMessages == null || now < mMessages.when)) {
                          pendingIdleHandlerCount = mIdleHandlers.size();
                      }
                      if (pendingIdleHandlerCount <= 0) {
                          mBlocked = true;
                          continue;
                      }
                      if (mPendingIdleHandlers == null) {
                          mPendingIdleHandlers = new IdleHandler[Math.max(pendingIdleHandlerCount, 4)];
                      }
                      mPendingIdleHandlers = mIdleHandlers.toArray(mPendingIdleHandlers);
                  }
                  for (int i = 0; i < pendingIdleHandlerCount; i++) {
                      final IdleHandler idler = mPendingIdleHandlers[i];
                      mPendingIdleHandlers[i] = null; // release the reference to the handler
                      boolean keep = false;
                      try {
                          keep = idler.queueIdle();
                      } catch (Throwable t) {
                          Log.wtf(TAG, "IdleHandler threw exception", t);
                      }
                      if (!keep) {
                          synchronized (this) {
                              mIdleHandlers.remove(idler);
                          }
                      }
                  }
                  pendingIdleHandlerCount = 0;
                  nextPollTimeoutMillis = 0;
              }
          }
      }

     ```

* 分析

  1. 以上代码略显复杂，但是最关键就是 enqueueMessage 方法和 next 方法，一个是插入数据，另一个是取出数据，数据保存在变量 mMessages 上；
  2. 不用深究代码，查看关键信息不难发现：这里是跨线程的关键点，局部变量 mMessages 可以在多个线程访问，next运行在当前线程，enqueueMessage 方法可以在新线程中访问，我们可以在新线程中调用enqueueMessage方法插入消息，next则会在当前线程不断轮询是否有新消息加入，若加入则返回Message对象，在当前线程接收消息；

##### Looper

* 开发中时常遇到这样的 bug，对付这样的问题，看看下面的源码就能水落石出。
	```java
	java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
	```

* 源码看看：
	```java
	public final class Looper {
    //显然，在不同线程会存有互不干扰的Looper实例
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new android.os.Looper(quitAllowed));
    }

    public static void loop() {
        final android.os.Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }
        final MessageQueue queue = me.mQueue;

        for (; ; ) {
            Message msg = queue.next(); // 若没有消息则阻塞在这里
            if (msg == null) {
                return;
            }

            final long traceTag = me.mTraceTag;
            if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
                Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
            }
            try {
                msg.target.dispatchMessage(msg);//分发消息
            } finally {
                if (traceTag != 0) {
                    Trace.traceEnd(traceTag);
                }
            }
            msg.recycleUnchecked();
        }
    }

    public static @Nullable Looper myLooper() {
        return sThreadLocal.get();
    }
}
	```

* 分析

  1. 先讲2个故事：

     > **故事1：**小明没有零花钱了，又不敢向爸爸妈妈，就想起了他奶奶。他奶奶家和他家距离不远也就几百米，于是他就到他奶奶家去了，但是小明奶奶家里没有现金了，而奶奶又年纪大了不方便去取钱，就打电话给银行要求银行送钱上门，但是银行工作人员办事效率不高，半天都没有送来，于是着急的小明只能空手而返了；过了一会他又去奶奶家看钱有没有送过来，结果银行工作人员还没有过来，小明只能再次空手而返......就这样不断来来回回去奶奶家看钱有没有送过来；
     > **故事2：**小红和小明一样没有零花钱却又不敢向爸爸妈妈要，也去找奶奶要；小红第一次去奶奶家，奶奶发现家里没有现金了，就打电话给银行要求送钱上门，而小红则陪着奶奶在家里等银行工作人员送前上门，奶奶心疼小红，不断打电话催银行快点快点；就这样小红每次去奶奶家要零花钱都是等拿到钱了再离开；

  2. 暂且不管上述故事剧情如何，其中小明是不管奶奶在不在家，去了就回来，有钱就拿钱，没钱就空手回家，而小红是去了就拿到钱才回来；回到Looper上来，`loop`方法就像是小红，而奶奶则是`MessageQueue`，`loop`方法调用`MessageQueue`的`next`方法，若`next`方法没有东西可返回就一直等着，并且不断询问有没有新消息过来；

  3. 要在新线程中使用`Looper`,需要初始化`Looper`，因为`Looper`对象是保存在`ThreadLocal`上的，所以在每个线程上都需要重新初始化，并且不能重复初始化；

  4. 开启消息循环需要调用`Looper.loop();`,结束时候别忘了要`Looper.myLooper().quit();`退出轮询；

### Handler

* 看完了以上的 ThreadLocal , MessageQueue , Looper，再看 Handler 就水到渠成了：

  ```java
  public class Handler {
      final MessageQueue mQueue;
  
      public final boolean sendMessage(Message msg) {
          return sendMessageDelayed(msg, 0);
      }
  
      public final boolean sendMessageDelayed(Message msg, long delayMillis) {
          if (delayMillis < 0) {
              delayMillis = 0;
          }
          return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
      }
  
      public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
          MessageQueue queue = mQueue;
          if (queue == null) {
              return false;
          }
          return enqueueMessage(queue, msg, uptimeMillis);
      }
  
      private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
          msg.target = this;
          if (mAsynchronous) {
              msg.setAsynchronous(true);
          }
          return queue.enqueueMessage(msg, uptimeMillis);
      }
  
      public void dispatchMessage(Message msg) {
          if (msg.callback != null) {
              handleCallback(msg);
          } else {
              if (mCallback != null) {
                  if (mCallback.handleMessage(msg)) {
                      return;
                  }
              }
              handleMessage(msg);//回调到熟悉的方法啦
          }
      }
  }
  ```

* 分析Handler是如何线程间传递的：
  1. 由熟悉的`sendMessage`开始，最后都是调用`enqueueMessage`方法，而最终还是调用`MessageQueue`类的`enqueueMessage`方法,到此为止我们一般都是在新线程调用；
  2. 接下来`Looper`类的`loop`方法会在当前线程（如果是更新UI则在主线程）从`MessageQueue`中获取最新消息，通过`msg.target.dispatchMessage(msg);`调用到`Handler`中来（上述代码），然后调用到`handlemessage(msg)`我们就熟悉啦；
  3. 由此，Handler跨线程最重要的是在当前线程（初始化`Handler`的线程）进行`loop`轮询，而变量是可以在不同线程访问·的，所以`Handler`可以在其他线程向`MessageQueue`中插入数据，而`loop`则在当前线程不断去取数据，取得数据就回调，达到跨线程的目的；

* 那么为什么 Handler 容易导致**内存泄漏**呢？
  1. 首先，内部类或者成员变量都会持有外部实例对象的引用
  2. Handler 在发送消息的时候，会将自己(Handler实例)也发送过去，见如上代码 `enqueueMessage`方法第一行：`msg.target = this;`
  3. 而这条 Message 会保存在 MessageQueue#mMessages 中,最终会保存在ThreadLocal中，意味着如果没有将该条 Message 及时消化掉的话，它会永远存在于主线程中；意味着和主线程生命周期保持一致的一个实力对象 ThreadLocal 中保存了一个 Activity 对象，而主线程的生命周期绝对要比 Activity 长，所以导致该 Activity 内存泄漏了。
  4. 所以，如果能保证在 Activity 销毁之前能将发出的 Message 全部消化掉，那也没事。

### Message 复用

`Handler#obtainMessage()`内部调用`Message#obtain()`方法：

```
/**
 * Return a new Message instance from the global pool. Allows us to
 * avoid allocating new objects in many cases.
 */
public static Message obtain() {
    synchronized (sPoolSync) {
        if (sPool != null) {
            Message m = sPool;
            sPool = m.next;
            m.next = null;
            m.flags = 0; // clear in-use flag
            sPoolSize--;
            return m;
        }
    }
    return new Message();
}
```



   