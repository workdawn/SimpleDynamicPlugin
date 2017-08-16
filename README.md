# SimpleDynamicPlugin
a simple android dynamic plugin (current version only support activity) use to study

How to use
---------------------
##### 1.  declared ```<activity android:name="com.workdawn.simpledynamicplugin.VirtualActivity"/>``` in your AndroidManifest.xml
##### 2.  init PluginManager in your Application class like this ```PluginManager.init(this);```
##### 3.  call loadPlugin method and you have two choice```PluginManager.loadPlugin(String pluginPath) or PluginManager.loadPlugin()```default plugin path is ```data/data/your packagename/app_d-plugin```
##### 4.  now you can call ```PluginManager.getInstance().startActivity(packagename, qualifiedClassName)``` or ```PluginManager.getInstance().startDefaultActivity()``` start target Activity