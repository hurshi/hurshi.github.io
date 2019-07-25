---
layout: post
title: Android 工具使用小技巧
catalog: true
tags:
    - Android
---

#### 技巧1: 加速 Gradle 构建

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

#### 技巧2：ADB

1. 安装 apk:`adb install -r ...apk`

2. 清空应用数据：`adb shell pm clean com.package.name`

3. 导出手机ANR信息:`adb pull /data/anr/traces.txt ~/Desktop/`

4. 无线调试

   ```shell
   # 连接设备
   adb connect 192.168.1.1:5555
   # 断开连接
   adb disconnect 192.168.1.1:5555
   ```

   