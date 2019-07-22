---
layout: post
title: Java 核心技术
subtitle: Java 易混淆归类
catalog: false
tags:
    - Java
    - 笔记
---

### String / StringBuilder / StringBuffer

* **StringBuffer** 是线程安全的，在并发情况下可以使用 StringBuffer
* **StringBuilder** 是线程不安全的，相比于 StringBuffer 拥有更好的性能，大多数情况下可以使用 StringBuilder.

  > 在 Java8 中，字符串相加会被转换为 StringBuilder 操作，而在 Java9 中，字符串相加会被 StringConcatFactory 统一优化。



### HashMap / HashTable / ConcurrentHashMap

* **HashMap**
  1. 线程不安全；
  2. JDK1.8后使用 **数组 + 链表** 的形式，在链表较大的时候还会转为红黑树。
* **~~HashTable(已废弃，推荐 ConcurrentHashMap)~~** 
  1. 继承自已被废弃的 Dictionary。
  2. 线程安全。
* **ConcurrentHashMap**: 
  1. 线程安全。
  2. 和HashTable相比，使用了分段锁，高并发情况下提高性能。

