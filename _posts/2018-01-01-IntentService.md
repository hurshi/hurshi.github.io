---
layout: post
catalog: true
tags:
    - Android
    - 源码
---

##### 先上源码：

```java
public abstract class IntentService extends Service {
      private volatile Looper mServiceLooper;
      private volatile ServiceHandler mServiceHandler;
      private String mName;
      private boolean mRedelivery;
  
      private final class ServiceHandler extends Handler {
          public ServiceHandler(Looper looper) {
              super(looper);
          }
  
          @Override
          public void handleMessage(Message msg) {
              onHandleIntent((Intent)msg.obj);
              stopSelf(msg.arg1);//👈若之后没事就尽快自杀吧
          }
      }
  
      public IntentService(String name) {
          super();
          mName = name;
      }
  
      public void setIntentRedelivery(boolean enabled) {
          mRedelivery = enabled;
      }
  
      @Override
      public void onCreate() {
          super.onCreate();
          HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
          thread.start();
  
          mServiceLooper = thread.getLooper();
          //👈关键点：指定handler的在HandlerThread中运行，HandlerThread其实就是一个普通的新线程
          mServiceHandler = new ServiceHandler(mServiceLooper);
      }
  
      @Override
      public void onStart(@Nullable Intent intent, int startId) {
          Message msg = mServiceHandler.obtainMessage();
          msg.arg1 = startId;
          msg.obj = intent;
          mServiceHandler.sendMessage(msg);
      }
  
      @Override
      public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
          onStart(intent, startId);
          return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
      }
  
      @Override
      public void onDestroy() {
          mServiceLooper.quit();
      }
  
      @Override
      @Nullable
      public IBinder onBind(Intent intent) {
          return null;
      }
  
      @WorkerThread
      protected abstract void onHandleIntent(@Nullable Intent intent);
}
```

#### 自己的理解：

1. 新建Thread HandlerThread；
2. 指定ServiceHandler运行在HandlerThread中；👈关键点
3. onStartCommand接收到消息全都send到ServiceHandler中；
4. ServiceHandler在新线程中handleMessage然后调用onHandleIntent;
