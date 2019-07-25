---
layout: post
title: Android Binder 
catalog: false
tags:
    - Android
---

### Binder 架构图

![](http://gityuan.com/images/binder/prepare/IPC-Binder.jpg)

图中的Client，Server，ServiceManager之间交互都是虚线表示，是由于它们彼此之间不是直接交互的，而是都通过与[Binder驱动](http://gityuan.com/2015/11/01/binder-driver/)进行交互的，从而实现 IPC 通信方式。其中 Binder驱动 位于内核空间，Client，Server，ServiceManager位于用户空间。

这四个角色的关系和互联网类似：Server是服务器，Client是客户终端，SMgr是域名服务器（DNS），驱动是路由器。

### 参考

* [Binder系列—开篇](http://gityuan.com/2015/10/31/binder-prepare/)

* [Android Bander设计与实现 - 设计篇](https://blog.csdn.net/universus/article/details/6211589)