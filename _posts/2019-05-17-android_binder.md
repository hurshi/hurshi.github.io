---
layout: post
title: Android Binder 
catalog: false
tags:
    - Android
---

### 概览：

1. **Binder 架构图**

   ![](/img/posts/android_binder/IPC-Binder.jpg)

   图中的Client，Server，ServiceManager之间交互都是虚线表示，是由于它们彼此之间不是直接交互的，而是都通过与[Binder驱动](http://gityuan.com/2015/11/01/binder-driver/)进行交互的，从而实现 IPC 通信方式。其中 Binder驱动 位于内核空间，Client，Server，ServiceManager位于用户空间。

   这四个角色的关系和互联网类似：Server是服务器，Client是客户终端，SMgr是域名服务器（DNS），驱动是路由器。

2. **C/S 模式**

   BpBinder(客户端)和BBinder(服务端)都是Android中Binder通信相关的代表，它们都从IBinder类中派生而来，关系图如下：

   ![](/img/posts/android_binder/Ibinder_classes.jpg)

   * client端：BpBinder.transact()来发送事务请求；
   * server端：BBinder.onTransact()会接收到相应事务。

3. **Binder 内存机制**

   ![](/img/posts/android_binder/binder_physical_memory.jpg)

   1. 只有1次内存拷贝：

      虚拟进程地址空间(vm_area_struct)和虚拟内核地址空间(vm_struct)都映射到同一块物理内存空间。当Client端与Server端发送数据时，Client（作为数据发送端）先从自己的进程空间把IPC通信数据`copy_from_user`拷贝到内核空间，而Server端（作为数据接收端）与内核共享数据，不再需要拷贝数据，而是通过内存地址空间的偏移量，即可获悉内存地址，整个过程只发生一次内存拷贝。一般地做法，需要Client端进程空间拷贝到内核空间，再由内核空间拷贝到Server进程空间，会发生两次拷贝。

   2. 为何不0次拷贝？共享同一块内存空间，对多进程的同步问题就比较复杂，安全性就更加复杂。

   3. 下面这图是从Binder在进程间数据通信的流程图，从图中更能明了Binder的内存转移关系。

      ![](/img/posts/android_binder/binder_memory_map.jpg)

### 第一步: 启动 ServiceManager



### 第二步：获取 ServiceManager



### 第三步： 注册服务



### 第四步：获取服务







### 参考

* [Binder系列—开篇](http://gityuan.com/2015/10/31/binder-prepare/)

* [Android Bander设计与实现 - 设计篇](https://blog.csdn.net/universus/article/details/6211589)