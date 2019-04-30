---
layout: post
title: 双亲委托么
catalog: true
tags:
    - Java
    - 源码
---


#### 概念理理顺

* 所谓的 双亲委托模型，还是它的英文名：`parent-delegation model` 比较好理解。（不要纠结"双亲"）
* 这里的 Parent 不是继承关系(extends)，他们并没有依赖或者实现了彼此的方法。而是类似于链表关系。

#### 还是看看源码吧

```java
// 为更好理解，本方法已精简，并非100%源码
protected Class<?> loadClass(String name, boolean resolve) {
    // 从本ClassLoader缓存中获取class ①
    Class<?> c = findLoadedClass(name);
    if (c == null) {
        try {
            if (parent != null) {
                // 如果本ClassLoader缓存中找不到，则从parent ClassLoader中去取 ②
                c = parent.loadClass(name, false);
            } else {
                // parent为null,说明已经到顶parent了，则使用Bootstrap ClassLoader去加载
                c = findBootstrapClassOrNull(name);
            }
        } catch (ClassNotFoundException e) {
        }
        // 从 parent 那里没有取到(包括缓存以及主动加载)，则自己来加载 ③
        if (c == null) {
            c = findClass(name);
            // 省略部分代码
        }
    }
    return c;
}
```

#### 分析

* 文字分析：由上述源码可以非常直观看出`parent-delegation model`机制：

  1. 首先在自己缓存中去取（上述 ①）
  2. 若取不到，则问 parent 要（上述 ②）
  3. 若 parent 那里也取不到，则自己去加载（上述  ③）

  而上述步骤2，在 parent 中，步骤也是上面的1,2,3.（递归了）

* 图片分析：
  1. 完整模型（来自 刘望舒《Android进阶解密》）

    ![](/img/posts/parents-delegation-model/mode.jpg ':size=300')

    * 结合上图显示，当一个加载 Class 请求到达一个 ClassLoader 的时候，首先去检查自己的缓存区有没有，若没有则委托给 parent, parent当然也是这么干的。当得知 parent 返回的是 null,那就没办法了，捋起袖子自己加载吧。

    * 其中，当找不到 parent时（到了最顶层），就是Bootstrap ClassLoader, 则调用`findBootstrapClassOrNull()`方法了，Bootstrap ClassLoader是一个特殊的ClassLoader,它是C/C++实现的。

  2. 精简模型：不是正确的模型，但是可以帮助理解，简化思维（来自 rengwuxian HenCoder）

    ![](/img/posts/parents-delegation-model/mode_simple.jpg ':size=300')

    上述图所示，可以简单理解为，先从公共缓存取，若取不到，则从 最顶层开始，若取不到则依次往下取，取到了就直接返回。这样理解起来就非常简单了，其实上面完整的模型图从流程上来说也是这样的，只不过把缓存区切割为多个，每个 ClassLoader各自管理而已。

#### 好处
摘抄自 刘望舒《Android进阶解密》：

1. 避免重复加载，如果已经加载过一次 Class，就不需要再次加载，而是直接读取已经加载的 Class。
2. 更加安全。如果不使用双亲委托模式，就可以自定义一个 String 类来替代系统的 String 类，这显然会造成安全隐患，采用双亲委托模式会使得系统的 String 类在 Java 虚拟机启动时就被加载，也就无法自定义 String 类来替代系统的 String 类，除非我们修改类加载器搜索的默认算法。还有一点，只有两个类名一致并且被同一个类加载器加载的类，Java 虚拟机才会认为他们是同一个类，想要骗过 Java 虚拟机显然不会那么容易。