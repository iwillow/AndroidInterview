# android优化
## ANR
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


