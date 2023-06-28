---
layout:     post
title:      "日常的设计模式"
subtitle:   "要像白开水一样"
date:       2023-06-28
author:     "Hurshi"
catalog: true
tags:
    - Design
---

# 六大原则
> `设计原则`并不是`设计模式`，通常一种设计模式会包含多种原则

### 单一职责原则
> 这个是最简单也是最难的

### 开闭原则

>  通过抽象和多态来实现来实现 对修改关闭，对拓展开放

### 里氏替换原则

>  通过继承和多态，子类不能更改父类的定义，能使用父类的地方，都能直接使用子类来替换；

### 依赖倒置原则

>  1.  高层模块不应该依赖低层模块，两者都应该依赖其抽象；
>  2.  抽象不应该依赖细节；
>  3.  细节应该依赖抽象

### 接口隔离原则

>  核心是需要将接口按功能模块隔离，让业务方可以“最小依赖”就能使用能力；
>
>  强调客户端不应该依赖于它不需要的接口。

### 迪米特原则

>  也称”最少知识原则“，上层应用不需要关心内部实现；
>
>  比如”一个蓝牙音乐播放SDK“，上层只需要调用”播放音乐“就可以了，不需要关心蓝牙是否开启等问题；

# 接下来是设计模式

>  所有设计模式都不应该破坏上述的六大原则，而是对六大原则的灵活应用；

### 单例模式

```java
package android.view;

public final class WindowManagerGlobal {
    public static WindowManagerGlobal getInstance() {
        synchronized (WindowManagerGlobal.class) {
            if (sDefaultWindowManager == null) {
                sDefaultWindowManager = new WindowManagerGlobal();
            }
            return sDefaultWindowManager;
        }
    }
```

Android Framework 中的 WindowManagerGlobal 使用了单例模式；以实现每个应用进程只有唯一的 WindowManager 与 FrameWork 通信；

### Builder 模式

```java
AlertDialog.Builder builder = new AlertDialog.Builder(context);
builder.setIcon(R.drawable.icon);
builder.setTitle("Title");
builder.setMessage("Message");
builder.create().show();
```

在设计`对外接口`的时候，使用 Builder 模式可以在后期拓展接口的时候做到向下兼容，比如：

```java
public void getMessage(long id, String name);
// 如果需要增加字段，为了向下兼容，就不得不增加一个接口，如下：
public void getMessage(long id, String name, String tag);
```

而如果使用 Builder 模式的话，则可以直接兼容掉：

```java
public void getMessage(Message.Builder builder);

class Message {
   class Builder {
      xxx
      setId(long id);
      setName(String name);
      setTag(String tag);// 新增字段
   }
}
```

### 策略模式

```java
// 时间插值器
public interface TimeInterpolator {
    float getInterpolation(float input);
}
```

```java
// 动画使用插值器
ObjectAnimator animator=ObjectAnimator.ofFloat(button,"alpha",1f,0f);
animator.setInterpolator(new LinearInterpolator());
animator.setDuration(2000);
animator.start();
```

在 Android 系统代码中，插值器动画就是典型的策略模式，**定义一套接口，有多个算法实现来满足不同的动画需求**。

比如局域网协议，为兼容不同版本，使用策略模式来实现多套协议的兼容，并保证后续的扩展性；

### 责任链模式

Android 源码中的 dispatchTouchEvent；

OKHttp 的责任链

### 享元模式

```java
public final class Message implements Parcelable {
    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }
```

Handler 中使用的  `Message.obtain` 就通过享元模式来避免 Message 的频繁创建销毁；同样的有 RecycleView / ListView 中使用的 ViewHolder

### 观察者模式

有 LiveData，Broadcast 等



# 实战

### 广告聚合SDK

>  设计一个广告聚合SDK，需要接入市面上大多数的广告SKD。这个SDK可以用到哪些设计模式

1. 工厂模式（Factory Pattern）：使用工厂模式来创建不同类型的广告 SDK 实例。通过定义一个广告 SDK 工厂类，根据传入的参数或配置信息，动态地创建相应的广告 SDK 对象。
2. 单例模式（Singleton Pattern）：在广告聚合 SDK 的核心管理类中，使用单例模式确保只有一个实例存在。这样可以方便地在整个应用程序中共享和访问广告聚合 SDK 的功能。
3. 适配器模式（Adapter Pattern）：由于市面上的广告 SDK 可能具有不同的接口和调用方式，可以使用适配器模式来统一这些不同的接口，使其符合广告聚合 SDK 的统一接口规范。
4. 外观模式（Facade Pattern）：广告聚合 SDK 可以提供一个简单易用的外观接口，隐藏底层各个广告 SDK 的复杂性。通过封装底层广告 SDK 的调用细节，提供统一的接口给应用程序使用。
5. 观察者模式（Observer Pattern）：广告聚合 SDK 可以提供回调接口，让应用程序可以注册观察者来监听广告加载、展示和点击等事件。当广告状态发生变化时，通知所有注册的观察者进行相应的处理。
6. 策略模式（Strategy Pattern）：广告聚合 SDK 可以根据不同的场景和需求，选择合适的广告策略进行展示。通过定义一个广告策略接口，并实现不同的广告策略类，可以在运行时动态地切换和选择合适的广告策略。比如横屏广告策略，原生广告策略，视频广告策略等等；
7. 缓存模式（Cache Pattern）：为了提高广告加载的效率和性能，可以使用缓存模式来缓存已经加载的广告数据。当需要展示广告时，首先从缓存中查找是否有可用的广告数据，避免重复请求和加载广告。

### Retrofit 

>  参考连接：https://blog.csdn.net/xmxkf/article/details/115636089

1.  Builder 模式

    ```java
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .build();
    ```

2.  工厂模式

    ```java
    public Retrofit build() {
        okhttp3.Call.Factory callFactory = this.callFactory;
        if (callFactory == null) {
            callFactory = new OkHttpClient();
        }
    ```

    ```java
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 👈
            .build();
    
    public interface CallAdapter<R, T> {
      abstract class Factory {
        public abstract @Nullable CallAdapter<xx> get(xx);
      }
    ```

    `addCallAdapterFactory`不仅仅使用了工厂模式，它内部还使用了 **适配器模式**，而且也是**策略模式**；

3.  抽象工厂模式

    ```java
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // 👈
            .build();
    
    public interface Converter<F, T> {
      @Nullable
      T convert(F value) throws IOException;
    
      abstract class Factory {
        public @Nullable Converter<xx> responseBodyConverter(xx) {}
        public @Nullable Converter<xx> requestBodyConverter(xx) {}
        public @Nullable Converter<xx> stringConverter(xx) {}
        protected static Type getParameterUpperBound(xx) {}
        protected static Class<?> getRawType(Type type) {}
      }
    }
    ```

4.  代理模式

    ```java
      public <T> T create(final Class<T> service) {
        return (T)
            Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class<?>[] {service},
                new InvocationHandler() {
                  private final Object[] emptyArgs = new Object[0];
    
                  @Override
                  public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
                      throws Throwable {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.getDeclaringClass() == Object.class) {
                      return method.invoke(this, args);
                    }
                    args = args != null ? args : emptyArgs;
                    Platform platform = Platform.get();
                    return platform.isDefaultMethod(method)
                        ? platform.invokeDefaultMethod(method, service, proxy, args)
                        : loadServiceMethod(method).invoke(args);
                  }
                });
      }
    ```

















