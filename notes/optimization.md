# android优化
## ANR优化
### 什么是ANR
在Android上，如果你的应用程序有一段时间响应不够灵敏，系统会向用户显示一个对话框，这个对话框称作应用程序无响应（ANR：Application Not Responding）对话框。用户可以选择“等待”而让程序继续运行，也可以选择“强制关闭”。
### 造成ANR原因
在主线程中做了耗时的操作;主线程被其他线程锁;cpu被其他进程占用，该进程没被分配到足够的cpu资源。这里的主线程包括Activity,Service的所有生命周期方法，BroadcastReceiver的onReceive方法。
没有使用子线程Looper的Handler的handleMessage、post（Runnable）方法。AsyncTask除了doInBackground的回调方法，
应用程序的响应性主要由ActivityManager和WindowManager系统服务监听。
经常出现ANR的情况:
 1. 按键或触摸事件在特定时间内（默认是5s）无响应
 2. BroadcastReceiver在特定时间内(10s)无法处理完成
 3. service 前台20s后台200s未完成启动 Timeout executing service
 4. ContentProvider的publish在10s内没进行完
### 避免办法
1. 将耗时的操作以转移到子线程（Handler、AsyncTask、IntentService）
2. 使用Thread或者HandlerThread**提高优先级**
3. 在Activity的声明周期回到方法中避免耗时操作


## 内存优化

### 什么是OOM
当前占用的内存加上新申请分配的内存超过了Dalvik虚拟机的所能分配的最大内存。经常会出现在加载大图的时候。

#### 解决方案
1. 如果是加载图片，最好提供缩略图或者压缩图片，在滑动的时候不去加载图片,不用的图片手动调用Bitmap.recycle()（注该方法释放native的内存）
2. BitmapFactory.Options的inSampleSize属性设置采样率，inBitmap属性保证复用之前Bitmap所分配的内存。


### 内存抖动

内存区域中大量的对象瞬间被创建然后又被回收掉，这个区域也被称为young generation区域。瞬间产生大量的对象会严重占用Young Generation的内存区域，当达到阀值，剩余空间不够的时候，也会触发GC。即使每次分配的对象占用了很少的内存，但是他们叠加在一起会增加Heap的压力，从而触发更多其他类型的GC。这个操作有可能会影响到帧率，并使得用户感知到性能问题。

#### 避免内存抖动的方法

1. 尽量避免在循环体内创建对象，应该把对象创建移到循环体外。
2. 注意自定义View的onDraw()方法会被频繁调用，所以在这里面不应该频繁的创建对象。
3. 当需要大量使用Bitmap的时候，试着把它们缓存在数组中实现复用。
4. 对于能够复用的对象，同理可以使用对象池将它们缓存起来。

### 内存泄漏

内存泄漏指的是那些程序不再使用的对象无法被GC识别，这样就导致这个对象一直留在内存当中，占用了宝贵的内存空间。显然，这还使得每级Generation的内存区域可用空间变小，GC就会更容易被触发，从而引起性能问题。

#### 出现场景&解决方案
1. 静态变量引起的内存泄漏（例如单例模式，中Context是Activity的而不是Application），解决办法传入的Context是Application
2. 非静态内部类引起的内存泄漏，解决方案：将非静态匿名内部类修改为静态匿名内部类
3. Handler引起的内存泄漏。将Handler改写成静态成员，静态类，退出时 Handler.removeCallbacksAndMessages(null);
4. 资源未关闭回收引起的内存泄漏，例如Cursor、BroadcastReceiver、Bitmap，当不需要使用时，需要及时释放掉，若没有释放，则会引起内存泄漏。
5. 动画未停止，在Activity的onDestroy或者View的onDetachFromWindow方法中cancel掉
6. AsyncTask未取消，在Activity的onDestroy中cancel掉

### 其他内存优化方案
1. Service完成任务后要尽量停止它
2. 在UI不可见的或者内存紧张的时候时候释放一些UI资源(onTrimMemory/OnLowMemory)例如Bitmap、数组、控件资源
3. 使用软引用或者弱引用（对于软引用，如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。对于弱引用，只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些只具有弱引用的对象。）
4. 使用针对内存优化过得容器，例如SparseArray/Pair/ArrayMap。
5. 采用内存缓存和磁盘缓存。
6. 尽量避免使用枚举
7. 避免使用依赖注入框架
8. 使用zip对齐APK
9. 采用多进程，将消耗内存的功能模块另外放置一个内存






