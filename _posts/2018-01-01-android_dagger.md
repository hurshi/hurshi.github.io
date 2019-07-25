---
layout: post
title: Dagger 知识补充
catalog: true
tags:
    - Android
    - Dagger
---

### @Dagger Scope 的生命周期

* 在 Dagger 2 官方文档中我找到一句话，非常清楚地描述了 @Scope 的原理：

  > When a binding uses a scope annotation, that means that the component object holds a reference to the bound object until the component object itself is garbage-collected.

  当 Component 与 Module、目标类（需要被注入依赖）使用 Scope 注解绑定时，意味着 Component 对象持有绑定的依赖实例的一个引用直到 Component 对象本身被回收。也就是作用域的原理，其实是让生成的依赖实例的生命周期与 Component 绑定，Scope 注解并不能保证生命周期，要想保证赖实例的生命周期，需要确保 Component 的生命周期。

* 参考: [Dagger 2 完全解析（二），进阶使用 Lazy、Qualifier、Scope 等 @ Johnny Shieh](http://johnnyshieh.me/posts/dagger-advance/)


### @Binds 初级使用

* 我的初步理解是：@Binds 是用来简化 @Provides 的，比如：

  ```java
  @Provides
  public clsss Parent(Child child){
      return child;
  }
  ```

  可以替换为：

  ```java
  @Binds
  public abstract Parent(Child child);
  ```

### Can @Provides and @Binds coexist

* 比较好的解决方案如下：

  ```java
  @Module(includes = ServletRequestAbstractModule.class)
  final class ServletRequestModule {
  	private final HttpServletRequest httpRequest;
  
  	@Provides
  	HttpServletRequestModule(HttpServletRequest httpRequest) {
  		this.httpRequest = httpRequest;
  	}
  
  	@Module
  	interface ServletRequestAbstractModule {
  		@Binds ServletRequest bindServletRequest(HttpServletRequest httpRequest);
  	}
  }
  ```

* 参考： [Dagger 2 annotations: can @Provides and @Binds coexist?](https://android.jlelse.eu/dagger-2-annotations-can-provides-and-binds-coexist-88079b9f6d27)

