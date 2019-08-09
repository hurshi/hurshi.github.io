---
layout: post
title: 单例
catalog: true
tags:
    - Java
    - 设计模式
---


原文：[@程序员小灰](https://mp.weixin.qq.com/s/2UYXNzgTCEZdEfuGIbcczA)

### 单例模式第一版
``` java
public class Singleton {
    private Singleton() {}  //私有构造函数
    private static Singleton instance;  //单例对象
    //静态工厂方法
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

### 单例模式第二版
在第一版的基础上加上双重加锁检测，防止并发时候创建多个对象
```java
public class Singleton {
   private Singleton() {}  //私有构造函数
   private static Singleton instance = null;  //单例对象
   //静态工厂方法
   public static Singleton getInstance() {
        if (instance == null) {      //双重检测机制
         synchronized (Singleton.class){  //同步锁
           if (instance == null) {     //双重检测机制
             instance = new Singleton();
               }
            }
         }
        return instance;
    }
}
```

### 单例模式第三版

```java
public class Singleton {
    private Singleton() {}  //私有构造函数
    private volatile static Singleton instance = null;  //单例对象
    //静态工厂方法
    public static Singleton getInstance() {
          if (instance == null) {      //双重检测机制
         synchronized (Singleton.class){  //同步锁
           if (instance == null) {     //双重检测机制
             instance = new Singleton();
                }
             }
          }
          return instance;
      }
}
```

**volatile**：防止指令重排，是很有必要的，`instance = new Singleton();`这段代码其实分为三部执行：

1. 为 instance 分配内存空间；
2. 初始化 instance;
3. 将 instance 指向分配的内存地址；

如果不添加 volatile ，执行顺序有可能变成 1->3->2，在多线程环境下就容易出问题。

### 单例模式第四版（推荐使用）
利用`ClassLoader`的加载机制来实现懒加载，并保证构建的单例线程安全。
```java
public class Singleton {
    private static class LazyHolder {
        private static final Singleton INSTANCE = new Singleton();
    }
    private Singleton (){}
    public static Singleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}
```

### 其他
* [详细的双语言(`Java`与`Kotlin`)5种单例模式](https://mp.weixin.qq.com/s?__biz=MzIwMzYwMTk1NA%3D%3D&mid=2247489158&idx=1&sn=9a72afd00f16607ed673b9750278cdad)