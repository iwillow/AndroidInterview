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
通过设置activity的`android:launchMode`属性来设置启动模式或者在启动activity的intent中代码中添加flags

#### standard模式
系统默认的模式，每次创建一个Activity都会重建一个实例，不管栈内是有这个Activity。从非Activity类型的Context(例如ApplicationContext、Service等)中以standard模式启动新的Activity是不行的，因为这类context并没有任务栈，所以需要为待启动Activity指定FLAG_ACTIVITY_NEW_TASK标志位。

#### singleTop模式
栈顶复用模式，在这种模式下，如果新的activity已经位于任务栈的顶部，那么不会创建新的activity，但是会调用onNewIntent方法，不会再调用onCreate、onStart、onResume方法。如果栈内没有这个activity的实例或者这个activity的实例不在栈顶，则仍然会创建新的activity。

#### singleTask模式
栈内复用模式，在这种情况下，如果在**同一个栈内**有这个activity，不论它在栈低还是栈顶，都不会创建新的实例，会将它上方的activity全部弹出，该模式具有`FLAG_ACTIVITY_CLEAR_TOP`效果，跟singleTop一样，系统会回调onNewIntent，是否是在同一个栈是在AndroidManifest.xml中activity的`android:taskAffinity`参数中设置，默认都是应用包名。
只有当启动模式为singleTask的时候，当TaskAffinity和allowTaskReparenting结合的时候，当一个应用A启动了应用B的某个Activity C后，如果Activity C的allowTaskReparenting属性设置为true的话，那么当应用B被启动后，系统会发现Activity C所需的任务栈存在了，就将Activity C从A的任务栈中转移到B的任务栈中。如果按home见再点击B的桌面图标，这个时候显示的不是B的助Activity，而是刚才被A启动的Activity C。

#### singleInstance模式

单实例模式，增强版的singleTask，除了具备singleTask模式的所有特性以外，该模式启动的栈始终位于新的任务战内。

### 通过代码设置启动模式

```
  Intent intent=new Intent();
  intent.setClass(ActivityA.this,ActivityB.class);
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  startActivity(intent);
```
如果代码和xml中都设置了启动模式，代码设置的有限级别更高，但是通过配置的方式无法设置`FLAG_ACTIVITY_CLEAR_TOP`，通过代码的方式无法设置singleInstance模式.
## Activity的Flags
`FLAG_ACTIVITY_NEW_TASK`类似于在xml文件中Activity设置`android:launchMode="singleTask"`
`FLAG_ACTIVITY_SINGLE_TOP`类似于在xml文件中Activity设置`android:launchMode="singleTop"`
`FLAG_ACTIVITY_CLEAR_TOP`会将同一个任务栈中所有位于它上面的activity出栈，如果被启动的activity的启动模式是singleTask，被启动的activity如果存在只是调用onNewIntent,如果不存在，创建新的activity，如果被启动的activity是standard模式
那么它连同之上的activity都要出栈，然后创建新的activity放入栈顶。
`FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS`具有这个标记的Activity不会出现在历史Activity列表中，当某些情况下我们不希望用户通过历史列表回到我们的Activity的时候这个标记比较有用，它等同于属性设置`android:excludeFromRecents="true"`。