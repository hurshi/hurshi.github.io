---
layout:     post
title:      "从 Choreographer 看绘制"
subtitle:   "绘制小马达"
date:       2023-06-21
author:     "Hurshi"
catalog: true
tags:
    - Draw
    - View
---

# 几个关键类

### WindowManager

1. WindowManager 是以服务的形式；

   ```java
   context.getSystemService(Context.WINDOW_SERVICE)
   ```

2. WindowManager 的实际能力由 WindowManagerGlobal 提供，它是一个<font color=red>单例</font>。

3. WindowManagerGlobal 内部维持一个 ViewRootImpl 列表

   ```java
   WindowManagerImpl
      ➜ WindowManagerGlobal -> singleInstance
         ➜ ArrayList<ViewRootImpl> mRoots
   ```

### Choreographer

1.  **请求与接收 vsync 信号**

    1.  核心是它的内部类 `FrameDisplayEventReceiver` ，核心能力是：

        1.  通过 native 接受系统发送的 vsync 信号；

        2.  请求下一个 vsync 信号；

    2.  核心方法如下

        ```java
        // 请求下一帧 vsync 事件
        public void scheduleVsync() {
           nativeScheduleVsync(mReceiverPtr);
        }
        
        private static native void nativeScheduleVsync(long receiverPtr);
        
        // vsync 回调，Called from native code.
        private void dispatchVsync(long timestampNanos, long physicalDisplayId, int frame,VsyncEventData vsyncEventData) {
           onVsync(timestampNanos, physicalDisplayId, frame, vsyncEventData);
        }
        
        public void onVsync(long timestampNanos, long physicalDisplayId, int frame,VsyncEventData vsyncEventData) {
           doFrame(mTimestampNanos, mFrame, mLastVsyncEventData);
        }
        ```

2.  **消息”蓄水池“**

    >  持有 `mCallbackQueues` 队列，将 `Input`，`Animation`，`Traversal`，`Commit` 事件保存起来，等待下次 vsync 信号到来后，再一次性消费；

    1.  mCallbackQueues 队列初始化
    
        ```java
        public static final int CALLBACK_INPUT = 0;
        public static final int CALLBACK_ANIMATION = 1;
        public static final int CALLBACK_INSETS_ANIMATION = 2;
        
        /**
         * Handles layout and draw.  
         * Runs after all other asynchronous messages have been handled.
         */
        public static final int CALLBACK_TRAVERSAL = 3;
        
        /**
         * Handles post-draw operations for the frame.
         * Runs after traversal completes.  
         */
        public static final int CALLBACK_COMMIT = 4;
        private static final int CALLBACK_LAST = CALLBACK_COMMIT;
        
        private final CallbackQueue[] mCallbackQueues;
        
        // 构造函数：
        private Choreographer(Looper looper, int vsyncSource) {
           // ...
           mCallbackQueues = new CallbackQueue[CALLBACK_LAST + 1];
           for (int i = 0; i <= CALLBACK_LAST; i++) {
              mCallbackQueues[i] = new CallbackQueue();
           }
        }
        ```
    
    2.  接收消息
    
        ```java
        private void postCallbackDelayedInternal(int callbackType, Object action, Object token, long delayMillis) {
           synchronized (mLock) {
              final long dueTime = SystemClock.uptimeMillis() + delayMillis;
              // 每个 callbackType 独立以链表存储各自的消息
              mCallbackQueues[callbackType].addCallbackLocked(dueTime, action, token);
           }
        }
        ```
    
    3.  消费消息
    
        ```java
        void doFrame(long frameTimeNanos, int frame, DisplayEventReceiver.VsxxData vd) {
            // ...
            doCallbacks(Choreographer.CALLBACK_INPUT, frameData, frameIntervalNanos);
            doCallbacks(Choreographer.CALLBACK_ANIMATION, frameData, frameIntervalNanos);
            doCallbacks(Choreographer.CALLBACK_INSETS_ANIMATION, frameData, frameIntervalNanos);
            doCallbacks(Choreographer.CALLBACK_TRAVERSAL, frameData, frameIntervalNanos);
            doCallbacks(Choreographer.CALLBACK_COMMIT, frameData, frameIntervalNanos);
        }
        ```

### ViewRootImpl

1. ViewRootImpl 实现了 View 和 WindowManager 之间所需要的协议；每个应用程序窗口都有一个对应的 ViewRootImpl 实例，比如 Activity。
2. ViewRootImpl 是应用程序和 Android framework 沟通的桥梁，包括持有 Choreographer 以交互视图信息；还有：
   1. WindowInputEventReceiver：接收屏幕点击触摸事件；
   1. InputStage：接收并处理输入法事件；
3. ViewRootImpl 中的 `performTraversals` 方法管理了 View 的 measure，layout，draw 三个核心步骤；




# 绘制初始化流程

```java
ActivityThread.handleLaunchActivity
   ➜ ActivityThread.performLaunchActivity
      ➜ Activity.attach // 初始化 window，并绑定 WindowManager
         ➜ mWindow = new PhoneWindow
         ➜ mWindow.setCallback(this);
         ➜ mWindow.setWindowManager(getSystemService(Context.WINDOW_SERVICE))
      ➜ Activity.onCreate // 在 onCreate 中 setContentView 流程
         ➜ Activity.setContentView
            ➜ mWindow.setContentView
               ➜ PhoneWindow.installDecor
                  ➜ new DecorView(PhoneWindow.this)
   
ActivityThread.handleResumeActivity
   ➜ ActivityThread.performResumeActivity
      ➜ Activity.onResume
      ➜ activity.mWindow.getWindowManager().addView(activity.mWindow.getDecorView(),xxx)
         ➜ WindowManagerGlobal.addView
            ➜ new ViewRootImpl
               ➜ new View.AttachInfo(ViewRootImpl.this)
               ➜ viewRootImpl.setView(decorView)
                  ➜ new Choreographer
                     ➜ new FrameDisplayEventReceiver
                  ➜ requestLayout // 请求 vsync 信号
                     ➜ mDisplayEventReceiver.scheduleVsync()
Choreographer.doFrame // 收到 vsync 信号
   ➜ doCallbacks(Choreographer.CALLBACK_INPUT, xxx);
   ➜ doCallbacks(Choreographer.CALLBACK_ANIMATION, xxx);
   ➜ doCallbacks(Choreographer.CALLBACK_INSETS_ANIMATION, xxx);
   ➜ doCallbacks(Choreographer.CALLBACK_TRAVERSAL, xxx);
      ➜ ViewRootImpl.doTraversal
      ➜ performTraversals
         ➜ DecorView.dispatchAttachedToWindow
            ➜ Activity.onAttachedToWindow // cb.onAttachedToWindow
         ➜ performMeasure
         ➜ performLayout
         ➜ performDraw
   ➜ doCallbacks(Choreographer.CALLBACK_COMMIT, xxx);   
```











