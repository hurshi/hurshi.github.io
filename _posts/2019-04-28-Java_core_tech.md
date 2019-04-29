---
layout: post
title: Java 核心技术
catalog: false
tags:
    - Java
    - 笔记
---

### String / StringBuilder / StringBuffer

1. **StringBuffer** 是线程安全的，在并发情况下可以使用 StringBuffer
2. **StringBuilder** 是线程不安全的，相比于 StringBuffer 拥有更好的性能，大多数情况下可以使用 StringBuilder.
3. 在 Java8 中，字符串相加会被转换为 StringBuilder 操作，而在 Java9 中，字符串相加会被 StringConcatFactory 统一优化。



### Else

