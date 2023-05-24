<img src=assets/title.png width=100% height=100% />

[![license](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0) ![release](https://img.shields.io/badge/release-v1.0.0-green.svg) ![Platform](https://img.shields.io/badge/platform-android-blue.svg) ![API](https://img.shields.io/badge/API-21-green.svg)
# Dynamic Plugin

As the company's business expands, the size of the App has also been increasing. Significant effects have been achieved through the past several App optimization optimizations. Conventional methods are now insufficient to reduce the size of the App any further. After analyzing the App, we found that some resources that are not frequently used and can be delivered, so we designed this SDK for dynamic resource loading.


# Features

- There are 3 built-in resources of predefined fonts, frame animations, SO, and 2 customizable resources such as single file and multiple files.
- Provides a common method for loading dynamic resources from which all resources can be loaded.
- Built-in resources, providing default application methods, external can be applied directly. Custom resources, users decide how to apply.
- For all resources, provide configurable packaging to reduce manual work.

# The effect used in the project

By introducing a Dynamic Resource Management System, and dynamically packaging some .so files and other ordinary resources, the package size of the LalaMove client has been reduced from 54M to 46M, a decrease of 8M. Subsequent efforts will continue to explore ways of further dynamic packaging for other files.

# Module Introduction
| Module            | Function          |
|-------------------|-------------------|
| sample_app        | Demo's app module |
| dynamic_res_plugin | Plugin module     |
| dynamic_res_base  | Base module       |
| dynamic_res_core  | Core module       |
| sample_native_lib | Demo's native library example    |
| dynamic_res_store | Generated resource folder          |
| dynamic_plugin.gradle | Plugin script              |


# Getting started
## 1.Compile and import plugin

1. Include dynamic_plugin in settings.gradle

   `include ':dynamic_plugin'`

2. Build dynamic_plugin module

3. Upload or uploadArchives dynamic_plugin module 

4. Add local maven as a repository in your main build.gradle in the root of your project

   `maven {url uri('./dynamic_plugin_repo')}`

5. Add dynamic_plugin as a dependency in your main build.gradle in the root of your project

   `classpath 'com.lalamove.dynamic_plugin:dynamic_res_plugin:1.0.0'`

6. Then you need to "apply" the plugin and add dependencies by adding the following lines to your app/build.gradle.

   `apply plugin: 'com.lalamove.dynamic_res_plugin'`

## 2.Import config

Apply the plugin in your main build.gradle in the root of your project
`apply from: 'dynamic_plugin.gradle'`

## 3.Generate Resources Package

1. Generate native library package

   Add "dynamic_config" into dynamic_plugin.gradle
   learn more at the sample dynamic_plugin.gradle

2. Generate other resources package

   Copy fonts, frame animations, and other resources to the path dynamic_res_store/input/

3. build application

   The plugin will generate the resources in the dynamic_res_store/output/ directory.

4. Upload resources

   Upload the resource to the server

5. Generate resource url ，fill into DynamicResConst.java

[learn more at 资源打包和使用说明.md](资源打包和使用说明.md)

## 4.Initialize

```java
DynamicConfig config = DynamicConfig.Builder.with(context)
        .executor()
        .loadSoManager(createLoadSoManager())
        .downlader()
        .debugLog(BuildConfig.DEBUG)
        .loggger()
        .monitor()
        .build();
DynamicResManager.getInstance().init(config);
```
learn more at the sample "DynamicInitJob.java"

## 5.Preloading

```java
IoThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
    if (DynamicResManager.getInstance().getLoadSoManager() != null) {
        DynamicResManager.getInstance().getLoadSoManager().loadSo(DynamicResConst.DEMO_SO, null);
    }
});
```
learn more at the sample PreDynamicLoadJob.java

## 6.Usage

1. Font

   ```
   DynamicResManager.getInstance().setTypeface(mFontTv, DynamicResConst.TypeFace.FONT_FZ_RZ_BOLD);
   ```

2. FrameAnimation

   ```
   mCarAnim = DynamicResManager.getInstance().createFrameAnim(ImageView).durations(100).oneShot(false);
   mCarAnim.startAnim(DynamicResConst.FrameAnim.ANIM_CAR);
   ```

3. so

   ```java
   DynamicResManager.getInstance().getLoadSoManager().loadSo(DynamicResConst.DEMO_SO, new ILoadSoListener() {
        @Override
        public void onSucceed(String path) {
            mContentTv.append(new NativeLib().stringFromJNI());
        }
   });
   ```


learn more at the sample MainActivity.java

## Communication
- If you find a bug, open an issue.
- If you have a feature request, open an issue.
- If you want to contribute, submit a pull request.

## Blog

   [货拉拉 Android 动态资源管理系统原理与实践](https://juejin.cn/post/7113703128733581342)

## Author

   [HUOLALA mobile technology team](https://juejin.cn/user/1768489241815070)

## License

Dynamic Plugin is released under the Apache 2.0 license. See [LICENSE](LICENSE) for details.