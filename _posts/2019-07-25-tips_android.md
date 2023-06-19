---
layout: post
title: Android 开发小技巧
catalog: true
tags:
    - Android
---

#### 技巧1：tools:node="remove"

1. WorkManager 初始化是这样的

   ```java
   WorkManager.initialize(context,configuration)
   
   public static void initialize(Context context, Configuration configuration) {
      synchronized (sLock) {
         if (sDelegatedInstance != null && sDefaultInstance != null) {
            throw new IllegalStateException("WorkManager is already initialized.  Did you "
               + "try to initialize it manually without disabling "
               + "WorkManagerInitializer? See "
               + "WorkManager#initialize(Context, Configuration) or the class level"
               + "Javadoc for more information.");
    }
    ...
   ```

	这里可以看到，WorkManager是不能被重复初始化的


2. 那么有没有对 AndroidManifest.xml 中的这段代码存在疑惑？

	```xml
   <provider
       android:name="androidx.work.impl.WorkManagerInitializer"
       android:authorities="${applicationId}.workmanager-init"
       android:directBootAware="false"
       android:exported="false"
       android:multiprocess="true"
       tools:node="remove"
       tools:targetApi="n" />

	```

	```java
   public class WorkManagerInitializer extends ContentProvider {
       @Override
       public boolean onCreate() {
           // Initialize WorkManager with the default configuration.
           WorkManager.initialize(getContext(), new Configuration.Builder().build());
           return true;
       }
       ...
	```

   说到底，这个 provider 也是执行初始化作用的，那么在 AndroidManifest.xml  中写这个 provider 不是重复初始化了么？

3. 这里的秘诀在于  tools:node="remove" 添加这个标签意味着将不会出现在 Merged Menifest 中，拓展一下，这个方法可以删除第三方SDK中申明的权限，比如

   ```xml
   <uses-permission android:name="android.permission.INTERNET" tools:node="remove"/>
   ```

4. 参考自
	1. [掘金 tools:node="remove"](https://juejin.im/entry/5c0f10496fb9a04a102f1f50)
	2. [官方文档](https://developer.android.com/studio/build/manifest-merge)



#### 技巧2：开发第三方类库自己初始化

1. 知识普及：ContentProvider 是在 Application 之前就去初始化的。
2. 能干啥事：利用这点可以干很多事，尤其是 SDK 开发，很多时候都要在文档中要求开发者在 Application 初始化，这时候完全可以定义一个 ContentProvider 自己去初始化，对接入的**开发者无感**了。
3. 有攻就有防：对付它就用上面的 **技巧1**。

#### 技巧3：Message.obtain()

1. 使用 `Message.obtain()` 来**复用** Message 对象。

2. 简单源码：

   ```java
   public final class Message implements Parcelable {
   
       Message next;
       private static Message sPool;
   
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
   
       //回收Message
       public void recycle() {
           ...
       }
   }
   ```

#### 技巧4: Tint 着色器

为 ImageView 添加属性`andorid:tint="@color/colorAccent"`即可改变 ImageView 上图片的显示颜色。

#### 技巧5: 复用图片

* 一张图片明明旋转下就满足要求了，就不要再创建另一张了：

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <rotate xmlns:android="http://schemas.android.com/apk/res/android"
      android:drawable="@mipmap/ic_launcher"
      android:fromDegrees="90"
      android:toDegrees="90" />
  ```

* 参考：[Android性能优化典范 - 第6季](https://blog.csdn.net/axi295309066/article/details/52658564)

#### 技巧6: 移除无用 String

* `appcompat-v7` 等三方库中包含大量的国际化资源，可以选择性的移除掉：

  ```groovy
  defaultConfig {
  	...
  	resConfigs('zh-rCN')
  }
  ```

* 参考：[Android 性能优化系列一 :APK极致优化](https://www.jianshu.com/p/147b54f53e10)    [使用resConfigs去除无用语言资源](https://www.jianshu.com/p/8796ad90fcc6)


#### 技巧7: Android 系统是如何安装 so 文件的？

1. 查看手机支持的架构：

   ```shell
   ✗ adb shell getprop ro.product.cpu.abilist
   ➜ arm64-v8a,armeabi-v7a,armeabi
   ```

2. APK 在安装的时候，会按照上面的顺序查看 apk 中是否有对应的文件夹，找到的话就将该文件夹下的所有 so 文件拷贝到 /data/app/packageName/lib/ 目录下，然后马上就停止查找了。

#### 技巧8: 减少冷启动

1. 用户点击返回键，默认就退出应用了，下次再点击的时候就是冷启动。

2. 在用户点返回的时候，使应用进入后台，而不是退出应用：

   ```java
   @Override
   public void onBackPressed(){
   	moveTaskToBack(true);
   }
   ```

3. 参考：[5分钟教你打造一个秒开的 Android App](https://mp.weixin.qq.com/s/a8076txSPIUqGAbe30uEug)

#### 技巧9：如何获得 bitmap 在内存中的大小

1. 系统方法：

   ```java
   public final int getByteCount() {
       return getRowBytes() * getHeight();
   }
   ```

2. 参考：[Android 开发绕不过的坑：你的 Bitmap 究竟占多大内存？](https://mp.weixin.qq.com/s?__biz=MzA3NTYzODYzMg==&mid=403263974&idx=1&sn=b0315addbc47f3c38e65d9c633a12cd6&scene=0&key=41ecb04b051110037b72d05bba1495f596e848534fc51afe877d63329a16dc24dc1d3606aaaba3745a05bfdb8c624a74&ascene=0&uin=Mjc3OTU3Nzk1&devicetype=iMac+MacBookPro10%2C1+OSX+OSX+10.10.5+build%2814F27%29&version=11020201&pass_ticket=kK4%2F6316QveG8O0vFtthPfBeKkNjyaL4HapsUAokHL5mUKCgI5hKTIKMc3D8uyqk)

#### 技巧10：FragmentTransaction.commit 的正确姿势

1. 不要随意使用 `FragmentTransaction.commitAllowingStateLoss()`来代替。
2. 推荐在 `onPostResume()` ,`onResumeFragment()`或`onCreate()`中调用 commit。



### 技巧11：绕过系统反射限制
1. Native hook 住 ShouldDenyAccessToMember 方法，直接返回 false

2. 破坏调用堆栈绕过去，使 VM 无法识别调用方：通过 JniEnv::AttachCurrentThread(…) 函数创建一个新的 Thread 来完成，

   >  Jni层新建个线程，在这个线程里去反射，去除掉了java调用的信息，从而让安卓系统以为这个是系统调用

3. 参考

   1. [Android 11 绕过反射限制](https://juejin.cn/post/7004723587307290637)
   2. [另一种绕过 Android P以上非公开API限制的办法](https://weishu.me/2019/03/16/another-free-reflection-above-android-p/)