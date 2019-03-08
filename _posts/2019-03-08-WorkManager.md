---
layout: post
title: Android WorkManager 解解惑
catalog: true
tags:
    - Android
---

### `tools:node="remove"`

1. WorkManager 初始化是这样的

  ```
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

   ```
   <provider
               android:name="androidx.work.impl.WorkManagerInitializer"
               android:authorities="${applicationId}.workmanager-init"
               android:directBootAware="false"
               android:exported="false"
               android:multiprocess="true"
               tools:node="remove"
               tools:targetApi="n" />
   ```

   ```
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

   ```
   <uses-permission android:name="android.permission.INTERNET" tools:node="remove"/>
   ```

4. 参考自[tools:node="remove"](https://juejin.im/entry/5c0f10496fb9a04a102f1f50)