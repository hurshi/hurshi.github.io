---
layout: post
title: Android 事件分发
catalog: false
tags:
    - Android
---

##### 上图：

![](/img/posts/touch_event.png)

##### 总结

1. `dispatchTouchEvent:`事件分发
2. `onInterceptTouchEvent:`事件拦截
3. `onTouchEvent:`事件处理，ACTION_DOWN的时候return true表示我要处理该事件，接下来的MOVE,UP等事件就会传进来，否则就不会再有其他事件传入。
3. `requestDisallowInterceptTouchEvent`: 子 View 请求 父 View 不要拦截；父 View 收到后，根据自己的判断是否需要调用 `onInterceptTouchEvent` 进行拦截
4. 看看视频就会了,[点我点我](https://v.qq.com/x/page/a0684ijwxzr.html)