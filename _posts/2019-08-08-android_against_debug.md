---
layout: post
title: Android 反调试与代码保护
catalog: false
tags:
    - Android
---

> 没有绝对的安全，只能做到相对更难破解而已。

#### Proguard：
对代码进行压缩(Shrink)，优化(Optimize)，混淆(Obfuscate)，检查(Veirfy)。
#### isDebuggerConnected()：
```java
//用来检查此刻是否有调试挂载到程序上。
if(android.os.Debug.isDebuggerConnected()){
	android.os.Process.killProcess(	android.os.Process.myPid());
}
```
#### android:debuggable 属性：

* 在 application 节点下加入：android:debuggable="false" 属性，使程序不能被调试。

* 在 Java 中也可以检测该属性：

  ```java
  if(getApplicationInfo().flag &= ApplicationInfo.FLAG_DEBUGGABLE !=0){
  	Log.i(TAG,"debug debug debug");
  	android.os.Process.killProcess(android.os.Process.myPid());
  }
  ```

#### keystore:

```kotlin
// 获得 keystore 信息
val sig = applicationContext.packageManager
	.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
	.signingInfo
......
// 然后在 Java 中比对或者上传服务器比对等方式来验证 keystore 的合法性。
```

#### 敏感信息如何保存

1. 隐藏在 so 文件中。
2. 隐藏在图片中。
3. 隐藏在逻辑中，可以通过 base64, hash, "异或"等等逻辑操作来隐藏。
4. 密钥分布在上面提到的各个地方，在程序中组合起来。

#### 其他：

比较省心的方式是使用市面上的加固服务

### 参考

1. [Android app反调试与代码保护的一些基本方案](https://cloud.tencent.com/developer/article/1427625)

   

   