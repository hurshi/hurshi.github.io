---
layout: post
title: Jvm内存模型 & Java内存模型
catalog: false
tags:
    - Java
    - 笔记
---

### Jvm 内存模型
![](/img/posts/java_memory_model/jvm.png){:width="90%"}

##### PC 寄存器（程序计数器）
1. 程序计数器是一块较小的内存空间，可以把它看作当前线程正在执行的字节码的行号指示器。
2. 字节码解释器通过改变程序计数器来依次读取指令，从而实现代码的流程控制，如：顺序执行、选择、循环、异常处理。
3. 特点：较小的存储空间；线程私有；随线程创建而创建，结束而死亡。

##### Java虚拟机栈
1. Java虚拟机栈会为每一个即将运行的Java方法创建一块叫做“栈帧”的区域，这块区域用于存储该方法在运行过程中所需要的一些信息，这些信息包括：
   1. 局部变量表（存放基本数据类型变量，引用类型变量，returnAddress 类型变量。
   2. 操作数栈
   3. 动态链接
   4. 方法出口信息等
2. 方法执行完毕，就会释放空间。
3. 线程私有。
4. 栈中的数据仅限于基本类型和对象引用。所以，在JVM中，栈上是无法保存真实的对象的，只能保存对象的引用。真正的对象要保存在堆中

##### 本地方法栈
和 “Java虚拟机栈” 类似，只是为“本地方法”服务的而已。

##### 堆
1. 堆事用来存放对象的内存空间。
2. **几乎所有**的对象都存储在堆中；垃圾回收的主要场所。
3. 线程共享，整个Java虚拟机只有一个堆。
4. 堆上也无法保存基本类型和对象引用。堆和栈分工明确。但是，对象的引用其实也是对象的一部分。
5. 数组是保存在堆上面的，即使是基本类型的数据，也是保存在堆中的。因为在Java中，数组是对象。

##### 方法区
1. 方法区市堆的一个逻辑部分。
2. 存放已经被虚拟机加载的类信息、常量、静态变量、即时编译器编译后的代码等。
3. 线程共享
4. 永久代 — 方法区中的信息一般需要长期存在。

##### 运行时常量池
1. 放三种数据：类信息、常量、静态变量、即时编译器编译后的代码。

##### JVM 内存模型小结
1. 一共2个栈，功能类似，线程私有，都是方法运行过程的内存模型。
2. 一共2个堆，一个原本的“堆”，一个方法区。
3. 堆是 Java虚拟机中最大的一块内存区域，也是垃圾收集器的主要工作区域。
4. 

### Java 内存模型
![](/img/posts/java_memory_model/java.png){:width="70%"}
主要解决并发编程时的原子性、有序性、一致性的问题。在Java中有一系列封装好的关键字比如`volatile`，`synchronized`，`final`，`concurren`。
##### 原子性：

`sychronized`保证方法和代码块内的操作是原子性的。

##### 可见性：

Java内存模型是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值的方式作为传递媒介来实现的。

##### 有序性：

volatile 防止指令重排，保证有序性。

##### synchronized: 

1. 线程解锁前，必须把共享变量的最新值刷新到主内存中；
2. 线程加锁时，将清空工作内存中的共享变量的值，从而使用共享变量时需要从主内存中重新读取最新的值。
3. 所以，synchronized 不仅能保证原子性，还能保证可见性。

##### volatile: 

1. 被volatile修饰的变量在修改后立即同步到主内存，每次用之前都从主内存刷新。
2. 禁止指令重排。
3. 所以，volatile 不能保证原子性。



### Java 对象模型

![](/img/posts/java_memory_model/java_obj.png){:width="90%"}

每一个Java类，在被JVM加载的时候，JVM会给这个类创建一个`instanceKlass`，保存在方法区，用来在JVM层表示该Java类。当我们在Java代码中，使用new创建一个对象的时候，JVM会创建一个`instanceOopDesc`对象，这个对象中包含了两部分信息，对象头以及元数据。对象头中有一些运行时数据，其中就包括和多线程相关的锁的信息。元数据其实维护的是指针，指向的是对象所属的类的`instanceKlass`。

### 参考

* [JVM内存结构 VS Java内存模型 VS Java对象模型](https://www.hollischuang.com/archives/2509)
* [深入理解JVM(一)——JVM内存模型](https://blog.csdn.net/qq_34173549/article/details/79612540)
* [再问你Java内存模型的时候别再给我讲堆栈方法区了](https://www.hollischuang.com/archives/3781)
* [再有人问你Java内存模型是什么，就把这篇文章发给他。](https://www.hollischuang.com/archives/2550)
* [Java虚拟机是如何执行线程同步的](https://www.hollischuang.com/archives/1876)
* [细说Java多线程之内存可见性](https://www.imooc.com/learn/352)
* [深入理解多线程（二）—— Java的对象模型](https://www.hollischuang.com/archives/1910)