---
layout: post
title: Android 工具使用小技巧
catalog: true
tags:
    - Android
---

#### 技巧1：加速 Gradle 构建

1. 清除构建缓存，强制刷新依赖库： `./gradlew --refresh-dependencies`

2. 查看Gradle性能：`./gradlew assembleDebug -profile`

3. 使用缓存

   ```groovy
   // gradle.properties:
   org.gradle.caching=true
   //build.gradle:
   kapt {
   useBuildCache = true
   }
   ```

4. 增加系统资源

   ```groovy
   // gradle.properties:
   org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8  // 配置编译时的虚拟机大小
   org.gradle.daemon=true // 开启线程守护，第一次编译时开线程，之后就不会再开了
   org.gradle.parallel=true  // 开启并行编译
   org.gradle.configureondemand=true // 启用新的孵化模式
   //build.gradle:
   dexOptions {
       incremental true //使用增量模式构建
       javaMaxHeapSize "4g"
       threadCount = 8 //线程数
   }
   ```

5. Debug 时跳过某些步骤

   1. 跳过 lint: 

      1. `./gradlew assembleDebug -x lint` 
      2. `project.gradle.startParameter.excludedTaskNames.add('lint')`

   2. 跳过 AAPT

      ```groovy
      aaptOptions {
          cruncherEnabled = false
      }
      ```

#### 技巧2：使用 ADB

1. 安装 apk：`adb install -r ...apk`

2. 查看系统事件日志：`adb -d logcat -b events -v time -d`

3. 系统 logcat 目录：`/system/etc/event-log-tags`

4. 清空应用数据：`adb shell pm clean com.package.name`

5. 无线调试

   ```shell
   # 连接设备
   adb connect 192.168.1.1:5555
   # 断开连接
   adb disconnect 192.168.1.1:5555
   ```


#### 技巧3：ANR 了怎么办

1. 不要 kill 进程，等待2～3分钟，日志需要收集以及读写时间。
2. 导出手机ANR信息：`adb pull /data/anr/traces.txt ~/Desktop/`
3.  详细信息见：[ANR 问题一般解决思路](https://www.jianshu.com/p/3959a601cea6)

#### 技巧4: 代码爆红怎么办

```shell
#!/bin/bash
# 1. 关闭本项目
# 2. 在 Android Studio 中删除本项目
# 3. 关掉 Android Studio
# 4. 执行本 Shell 脚本

rm -rf .idea/ && rm -rf .gradle/ && find ./ -name '*.iml' -type f -delete

# 5. 然后在 Android Studio 中重新 open 就行了
```

#### 技巧5：查看执行时间

1. **查看应用启动时间**：从 Android KitKat 开始，Logcat 中会输出从程序启动到某个 Activity 显示到画面上所花费的时间

   ```
   ***I/ActivityManager: Displayed io.github.hurshi.androidtest/.MainActivity: +588ms
   ```

2. **Method Tracing**：能查看方法耗时的细节。

3. 使用 Systrace 的 `trace.beginSection(String name)` 和 `trace.endSection()`。

4. 参考：[Android性能优化典范 - 第6季](http://hukai.me/android-performance-patterns-season-6/)

#### 技巧6: 某些文件 AS 不识别

> Preferences => File Types => 在 Recognized File Types 中找到 Text => 在 Registered Patterns 中找到有异常的文件 => 把它删掉把它删掉

   