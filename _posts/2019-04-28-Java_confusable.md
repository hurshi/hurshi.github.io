---
layout: post
title: Java 易混淆归类
catalog: false
tags:
    - Java
    - 笔记
---

### String / StringBuilder / StringBuffer

* **StringBuffer** 是线程安全的，在并发情况下可以使用 StringBuffer
* **StringBuilder** 是线程不安全的，相比于 StringBuffer 拥有更好的性能，大多数情况下可以使用 StringBuilder.

  > 在 Java8 中，字符串相加会被转换为 StringBuilder 操作，而在 Java9 中，字符串相加会被 StringConcatFactory 统一优化。



### HashMap / ~~HashTable~~ / ConcurrentHashMap

* **HashMap**
  1. 线程不安全；
  2. JDK1.8后使用 **数组 + 链表** 的形式，在链表较大的时候还会转为红黑树。
* **~~HashTable(已废弃，推荐 ConcurrentHashMap)~~** 
  1. 继承自已被废弃的 Dictionary。
  2. 线程安全。
* **ConcurrentHashMap**: 
  1. 线程安全。
  2. 和 HashTable 相比，使用了**分段锁**，高并发情况下相比于 HashTable 的**全表锁**提高不少性能。
  3. 主干是 Segment 数组，而 Segment 是一个继承了 ReentrantLock 的哈希表。
* **SparseArray**:
  1. 使用上类似于 HashMap，但**在某些情况下**比 HashMap 有更好的性能。这里的“某些情况”指的是：
     1. 数据量不是很大；（数据量大的时候，查找的时候二分法效率明显没有 hash 高）。
     2. key 值是 int 类型；
  2. 使用数组存储，使用2个数组分别存放 Key、value。
  3. 使用二分法查找 key ，这点和 HashMap 相比是不足之处。
* **LinkedHashMap**:
  1. 在 HashMap 的基础上，使用双向链表实现了保留插入顺序的功能。
* **HashSet**:
  1. 底层使用 HashMap 实现，将数据保存在 HashMap 的 key 中。

### Vector / ArrayList / LinkedList / CopyOnWriteArrayList

* **Vector**:
  1. 线程安全。
* **ArrayList**:
  1. 线程不安全。
  2. 内部使用数组实现。
* **LinkedList**:
  1. 线程不安全。
  2. 内部使用链表实现。
* **CopyOnWriteArrayList**:
  1. 线程安全。使用 ReentrantLock。
  2. 内部使用的是用 volatile transient 声明的数组Array。
  3. 读写分离，写时复制出一个新数组，完成插入、修改或移除操作后将新的数组赋值给 array。

### 乐观锁 / 悲观锁

* 乐观锁：总是假设最坏的情况，每次去拿数据的时候都认为别人会修改，所以每次在拿数据的时候都会上锁，这样别人想拿这个数据就会阻塞直到它拿到锁。传统的关系型数据库里边就用到了很多这种锁机制，比如行锁，表锁等，读锁，写锁等，都是在做操作之前先上锁。Java中`synchronized`和`ReentrantLock`等独占锁就是悲观锁思想的实现。
* 悲观锁：总是假设最好的情况，每次去拿数据的时候都认为别人不会修改，所以不会上锁，但是在更新的时候会判断一下在此期间别人有没有去更新这个数据，可以使用版本号机制和CAS算法实现。**乐观锁适用于多读的应用类型，这样可以提高吞吐量**，在Java中`java.util.concurrent.atomic`包下面的原子变量类就是使用了乐观锁的一种实现方式**CAS**实现的。
* 摘录自： [可能是全网最好的MySQL重要知识点 | 面试必备](https://mp.weixin.qq.com/s/S9jiO_e-_CKRgNnzAU5Z0Q)   /    [面试必备之乐观锁与悲观锁](https://github.com/Snailclimb/JavaGuide/tree/master/docs/essential-content-for-interview)

### 参考

1. [极客学院 -- Java集合学习指南](http://wiki.jikexueyuan.com/project/java-collection/)
2. 鱼哥的知识星球