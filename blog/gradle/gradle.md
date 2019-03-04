# Gradle 看过来

### 小白指令
* 清除缓存，强制刷新依赖库：`./gradlew --refresh-dependencies`



### 加速构建

查看Gradle性能：`./gradlew assembleDebug -profile`

1. 使用缓存：

   ```groovy
   // gradle.properties:
   org.gradle.caching=true
   //build.gradle:
   kapt {
   	useBuildCache = true
   }
   	
   ```

2. 增加系统资源

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

3. **Debug** 时跳过某些步骤

   1. 跳过 lint
      1. `./gradlew assembleDebug -x lint` ：在gradle命令后面跟上` -x lint`
      2. `project.gradle.startParameter.excludedTaskNames.add('lint')`

   2. 跳过 AAPT

      ```groovy
      aaptOptions {
          cruncherEnabled = false
      }
      ```





### QA

* <u>build.gradle 中的 gradle 版本号</u> 和 <u>gradle-wrapper.properties 中的版本号</u>有什么区别

  **答**：build.gradle中描述的是Google为Android开发的Gradle插件版本，而wrapper中描述的是由[gradle.org](https://gradle.org)维护的Gradle的版本。

  有点类似于：有一个lib库依赖于java版本，build.gradle对应于lib版本，而wrapper对应于java版本。

