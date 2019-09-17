---
layout: post
title: Android stack and launch mode
catalog: false
tags:
    - Android
---

### 概述

1. 启动新的 Activity，该把它放到哪个 stack 中呢？这是由启动模式以及 taskAffinity 共同作用的。
2. 在一个 stack 中，可以放来自不同应用的 activity。

### 如果 ActivityA 要启动 ActivityB:

1. 如果 ActivityB 是 standard 或者 singleTop，那么  ActivityB 会被直接放到 ActivityA 所在的 stack 中。（除非 ActivityA 是 singleInstance 的，这个之后讨论）
2. 如果 ActivityB 是 SingleTask 或者 Intent.FLAG_ACTIVITY_NEW_TASK 的，意思就是说：”你能尊重下我 taskAffinity 么？”，所以系统会查找 ActivityB 的 taskAffinity 属性，并将 ActivityB 放到 taskAffinity 一致的 stack 中（没有就创建一个）。
3. 如果 ActivityB 是 SingleInstance 的，那就是说：“我 ActivityB 是山大王，我不能忍受别人在我的 stack 中”。所以无论如何，系统都会创建一个 ActivityB 指定的 taskAffinity 的 stack给他用，即使 taskAffinity 重名也无所谓。



### 参考

1. [讲讲Android的launchMode,taskAffinity,以及Intent Flags](https://www.jianshu.com/p/a0d93dc6bca5)
2. [Android总结篇系列：Activity Intent Flags及Task相关属性](https://www.cnblogs.com/lwbqqyumidi/p/3775479.html)