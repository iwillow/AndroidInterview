# Activity

## 启动周期

初次：oncreate->onStart->onResume(可见)。

按home键或锁屏：onPause->onSaveInstanceState(Bundle outState)->onStop(不可见)，（有些手机按锁屏键可能会调用onDestroy）。

再回到应用：onRestart->onStart->onResume

屏幕旋转:onPause->onSaveInstanceState(Bundle outState)->onStop->onDestroy->onCreate->onStart-> onRestoreInstanceState(Bundle savedInstanceState)->onResume

如果配置了 android:configChanges="screenSize|orientation"，自从Android 3.2（API 13），screen size也开始跟着设备的横竖切换而改变。所以，在AndroidManifest.xml里设置的MiniSdkVersion和TargetSdkVersion属性大于等于13的情况下，如果你想阻止程序在运行时重新加载Activity，除了设置”orientation“，你还必须设置”ScreenSize”，就像这样子，android:configChanges=”orientation|screenSize”。但是呢，如果你的Target API 级别小于13，你的Activity自己会自动处理这种ScreenSize的变化。如果你的TargetSdkVersion小于13，即使你在Android 3.2或者更高级别的机器上运行程序，它还是会自己去处理ScreenSize的。

如果ActivityA启动ActivityB，两者个周期依次是
MainActivityA#onPause->MainActivityB#onCreate->MainActivityB#onStart->MainActivityB#onResume->MainActivityA#onSaveInstanceState(Bundle outState)->MainActivityA#onStop


## 启动模式


