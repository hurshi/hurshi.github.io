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
      WindowManagerGlobal -> singleInstance
      - ArrayList<ViewRootImpl> mRoots
   ```
### Choreographer
1.  核心是它的内部类 `FrameDisplayEventReceiver` ，核心能力是：

    1.  通过 native 接受系统发送的 vsync 信号；

    1.  请求下一个 vsync 信号；

2.  核心方法如下

    ```java
    private static native void nativeScheduleVsync(long receiverPtr);
    
    // Called from native code.
    private void dispatchVsync(long timestampNanos, long physicalDisplayId, int frame,
                               VsyncEventData vsyncEventData) {
       onVsync(timestampNanos, physicalDisplayId, frame, vsyncEventData);
    }
    
    public void scheduleVsync() {
       nativeScheduleVsync(mReceiverPtr);
    }
    
    public void onVsync(long timestampNanos, long physicalDisplayId, int frame,
                    VsyncEventData vsyncEventData) {
       doFrame(mTimestampNanos, mFrame, mLastVsyncEventData);
    }
    ```

### ViewRootImpl

1. 



# SetContentView

```java
ActivityThread.handleLaunchActivity
   ➜ ActivityThread.performLaunchActivity
   	➜ Activity.attach
		   ➜ mWindow = new PhoneWindow
		   ➜ mWindow.setCallback(this);
   		➜ mWIndow.setWindowManager(getSystemService(Context.WINDOW_SERVICE))
   	➜ Activity.onCreate
   		➜ Activity.setContentView
   			➜ mWindow.setContentView
	   			➜ PhoneWindow.installDecor
   					➜ new DecorView(PhoneWindow.this)
   
ActivityThread.handleResumeActivity
   ➜ ActivityThread.performResumeActivity
   	➜ Activity.onResume
   	➜ activity.mWindow.getWindowManager().addView(
   							activity.mWindow.getDecorView(),xxx)
   		➜ WindowManagerGlobal.addView
   			➜ new ViewRootImpl
	   			➜ new View.AttachInfo(ViewRootImpl.this)
   				➜ viewRootImpl.setView(decorView)
   					➜ new Choreographer
	   					➜ new FrameDisplayEventReceiver
   					➜ requestLayout
   						➜ mDisplayEventReceiver.scheduleVsync()
Choreographer.doFrame
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











