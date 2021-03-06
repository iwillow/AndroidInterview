# Activity

[更全面的总结参考](http://javayhu.me/blog/2015/11/30/art-of-android-development-reading-notes-1/)

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


## IntentFilter
IntentFilter 主要用于隐式调用android系统组件如Activity、Service、BroadcastReceiver做过滤Intent使用,可以有多个IntentFilter，每一个可以有多个IntentFilter可以有多个
action、category、data.一个过滤列表中的action、category、data可以有多个，所有的action、category、data分别构成不同类别，同一类别的信息共同约束当前类别的匹配过程。只有一个Intent同时匹配action类别、category类别和data类别才算完全匹配，只有完全匹配才能成功启动目标Activity。此外，一个Activity中可以有多个intent-filter，一个Intent只要能匹配任何一组intent-filter即可成功启动对应的Activity。
```
<activity android:name=".ShareActivity">

<intent-filter>
    <action android:name="com.iwillow.app.action.actionA" />
    <action android:name="com.iwillow.app.action.actionB" />

    <category android:name="com.iwillow.app.category.categoryA" />
    <category android:name="com.iwillow.app.category.categoryB" />
    <category android:name="android.intent.category.DEFAULT" />

    <data android:mimeType="text/plain" />
</intent-filter>

<intent-filter>
    <action android:name="com.iwillow.app.action.actionA" />
    <action android:name="com.iwillow.app.action.actionB" />

    <category android:name="com.iwillow.app.category.categoryA" />
    <category android:name="com.iwillow.app.category.categoryB" />
    <category android:name="android.intent.category.DEFAULT" />

    <data android:mimeType="image/*" />
	<data android:mimeType="video/*" />
</intent-filter>

</activity>
```
### action匹配规则
只要Intent中的action能够和过滤规则中的任何一个action相同即可匹配成功，action的匹配区分大小写。

### category匹配规则
Intent中如果有category那么所有的category都必须和过滤规则中的其中一个category相同，如果没有category的话那么就是默认的category，即android.intent.category.DEFAULT，所以为了Activity能够接收隐式调用，配置多个category的时候必须加上默认的category。
### data匹配规则
与action的匹配规则类似，如果过滤规则中定义了data，那么Intent中必须也要定义可匹配的data.data的格式如下：
```
<data android:scheme="string"
  android:host="string"
  android:port="string"
  android:path="string"
  android:pathPattern="string"
  android:pathPrefix="string"
  android:mimeType="string" />
```

主要由mimeType和URI组成，其中mimeType代表媒体类型，而URI的结构也复杂，大致如下：
<scheme>://<host>:<port>/[<path>]|[<pathPrefix>]|[pathPattern] 例如content://com.example.project:200/folder/subfolder/etc scheme、host、port分别表示URI的模式、主机名和端口号，其中如果scheme或者host未指定那么URI就无效。 path、pathPattern、pathPrefix都是表示路径信息，其中path表示完整的路径信息，pathPrefix表示路径的前缀信息；pathPattern表示完整的路径，但是它里面包含了通配符(*)。

data匹配规则：Intent中必须含有data数据，并且data数据能够完全匹配过滤规则中的某一个data。
URI有默认的scheme！

如果过滤规则中的`mimeType`指定`为image/*`或者`text/*`等这种类型的话，那么即使过滤规则中没有指定URI，URI有默认的scheme是`content`和`file`！如果过滤规则中指定了scheme的话那就不是默认的scheme了。
```
//URI有默认值
<intent-filter>
    <data android:mimeType="image/*"/>
    ...
</intent-filter>
//URI默认值被覆盖
<intent-filter>
    <data android:mimeType="image/*" android:scheme="http" .../>
    ...
</intent-filter>
```
如果要为Intent指定完整的data，必须要调用setDataAndType方法！不能先调用setData然后调用setType，因为这两个方法会彼此清除对方的值

```
intent.setDataAndType(Uri.parse("file://abc"), "image/png");
```
data的下面两种写法作用是一样的：
```
<intent-filter>
    <data android:scheme="file" android:host="www.github.com"/>
</intent-filter>

<intent-filter>
    <data android:scheme="file"/>
    <data android:host="www.github.com"/>
</intent-filter>
```

#### 判断某一Intent是否存在
1. PackageManager的`resolveActivity`方法或者Intent的`resolveActivity`方法：如果找不到就会返回null
2. PackageManager的`queryIntentActivities`方法：它返回所有成功匹配的Activity信息 针对Service和BroadcastReceiver等组件，PackageManager同样提供了类似的方法去获取成功匹配的组件信息，例如`queryIntentServices`、`queryBroadcastReceivers`等方法
