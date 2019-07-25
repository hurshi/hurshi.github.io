---
layout: post
title: Android MVVM 模块化架构
catalog: false
tags:
    - 架构
---

### 序

1. 这次的MVVM不是传统上的，而是需要配合Google的 ViewModel,LiveData使用的。
2. 严重使用了 Dagger2，轻度依赖了ARouter。

### ViewModel

1. ViewModel 是要交给 Dagger 来管理的，比如：

   ```kotlin
   class UserActivityVM @Inject constructor(application: Application): BaseViewModel() {}
   ```

2. ViewModel 不知道它是给哪个 View (MVVM中的V)提供服务的，也就是说它不能持有 Activity 实例，甚至不能有Context出现，当然ApplicationContext除外。

3. ViewModel的生命周期是要比 Activity/Fragment  长的，所有 ViewModel 中的对 View 的更新都使用 LiveData 让 View 订阅更新。

### View 

1. 使用 Dagger2 将需要的 ViewModel Inject进来，因为这个是通用操作，可以写在 BaseActivity/BaseFragment中，例如：

   ```kotlin
   @Inject
   lateinit var factory: ViewModelProvider.Factory
   
   val viewModel: T by lazy {
       val observer = ViewModelProviders.of(this, factory).get((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>)
       lifecycle.addObserver(observer)
       observer
   }
   ```

2. 在 Activity/Fragment 中订阅 ViewModel 中的 LiveData,以获取需要的数据。比如：

   ```kotlin
   viewModel.nickName.observe(this, Observer { if (null != it) nickName.text = it })
   ```

3. Activity/Fragment 不能调用 ViewModel中的耗时方法（因为ViewModel的生命周期是比View长的），Activity/Fragment 从 ViewModel 获取的所有数据都只能通过 LiveData 获取，而不能直接调用返回结果。

4.  番外：在View中使用[SimplifyDagger](https://github.com/hurshi/simplifydagger)可以少些好几吨 Dagger 代码。

### 模块化

1. 一些公用模块，比如 Utils,Resources,Widgets等业务逻辑无关的，可以被其他任何一个模块依赖。
2. 创建 DaggerModule,用来管理所有业务模块。
3. 业务模块A，B，C等，互相之间不能有依赖关系，全部被DaggerModule依赖。

### 模块间通信（api化）

1. 若模块A想暴露某个功能给其他模块使用，则在`_apis_`文件夹下写一个 interface,然后在其他地方实现这个 interface,然后交给 dagger：

   ```kotlin
   @AppScope
   @Binds
   public abstract UserInfo privideUserInfo(UserInfoImpl impl);
   ```

2. 将 `_apis_`文件夹暴露出去：

   1. gradle脚本
   
      ```groovy
      apply plugin: 'maven-publish'
         
         task sourceJar(type: Jar) {
             from fileTree(dir: "${project.projectDir.absolutePath}/build/intermediates/javac/debug/classes", include: '**/_apis_/**/*.class')
             from fileTree(dir: "${project.projectDir.absolutePath}/build/tmp/kapt3/incrementalData/debug", include: '**/_apis_/**/*.class')
         }
         
         publishing {
             publications {
                 bar(MavenPublication) {
                     groupId 'io.github.hurshi'
                     artifactId project.name
                     version '1.0'
                     artifact(sourceJar)
                 }
             }
             repositories {
                 maven {
                     url "${rootProject.projectDir.absolutePath}/repo"
                 }
             }
         }
      ```
   
   2. shell 脚本
   
      ```shell
      ./gradlew clean -p ../
      ./gradlew assembleDebug -p ../
      ./gradlew publish -p ../
      ```
   
   3. 在需要使用这个模块的 build.gradle 中添加：
   
      ```groovy
      compileOnly "io.github.hurshi:UserInfo:1.0"
      ```
   
   4. 这样，我们就将`_apis_`文件夹下的 interface 都暴露出去了，以此完成模块间的通信。

3. **模块间通信原理**：我们写一个 interface，然后把实现这个 interface 的`实例Z`交给 dagger,当其他模块拿着这个interface 问dagger要实例的时候，dagger 就会把`实例Z`返回，其实是Dagger在帮你把`实例Z`从模块A拿到了其他模块。而`_apis_`相当于把`实例Z`的"key"拿到了其他模块（interface相当于是`实例Z`的"key").



### 其他

1. 理论上使用上述的方案已经能完成模块间通信的需求了，但对于 Activity 的跳转，还是使用 ARouter 比较方便。（如果觉得Arouter比较重，完全可以不用，只要Dagger就能实现模块化开发了）