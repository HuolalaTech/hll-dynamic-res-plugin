<img src=assets/title.png width=100% height=100% />

[![license](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0) ![release](https://img.shields.io/badge/release-v1.0.0-green.svg) ![Platform](https://img.shields.io/badge/platform-android-blue.svg) ![API](https://img.shields.io/badge/API-21-green.svg)
# 背景

随着公司业务的扩展，App的体积也不断变大，在过去的几期App瘦身优化中，取得了显著的效果。常规的方法已经很难再缩小app的体积。在对App进行分析后，我们发现可以将部分不常用到、可以下发的资源， 所以我们设计了这个SDK用于资源的动态加载。

# 功能介绍

- 资源分类，预定义了字体，帧动画，so这3种内置资源，以及单个文件，多个文件这2种可自定义资源。
- 提供通用的加载动态资源方法，所有资源均可由此加载。
- 内置资源，提供默认的应用方法，外部可以直接应用。自定义资源，用户自行决定如何应用。
- 对于所有资源，提供可配置的方便快捷打包方式，减少手动操作。

# 项目中使用的效果

通过引入动态资源管理系统，并将部分的so文件和其他普通资源动态化后，货拉拉用户端的包体积减少了8M，从54M变为了46M。后继将会继续尝试进行其他文件的动态化。

<video src="assets/demo.mp4" width="360px" height="800px" controls="controls"></video>

# 工程介绍
| 模块                    | 功能               |
|-----------------------| ------------------ |
| sample_app            | demo的 app模块     |
| dynamic_res_plugin    | 插件模块           |
| dynamic_res_base      | 基础库             |
| dynamic_res_core      | 动态资源核心库     |
| sample_native_lib     | demo的native库示例 |
| dynamic_res_store     | 生成的资源文件夹   |
| dynamic_plugin.gradle | 插件脚本           |


# 使用介绍
## 1.编译和引入插件
1. 模块中添加依赖
   
   `classpath 'cn.huolala:dynamic_res_core:1.1.1'`

2. 主目录下，build.gradle里面添加依赖
   
   `classpath 'cn.huolala:dynamic_res_plugin:1.1.1'`

3. app/build.gradle里面添加
   
   `apply plugin: 'com.lalamove.dynamic_res_plugin'`

## 2.增加gradle配置

主目录下，build.gradle里面添加

`apply from: 'dynamic_plugin.gradle'`

## 3.打包资源

1. so资源打包

   在dynamic_plugin.gradle中配置dynamic_config，详细内容请看dynamic_plugin.gradle

2. 其他资源打包

   将字体、帧动画、以及其他资源拷贝到dynamic_res_store/input/中对应目录

3. 运行app

   在dynamic_res_store/output/目录会生成对应的资源。

4. 上传资源

   自行将资源上传到服务器（也可以自行在代码中增加上传逻辑）

5. 生成url填写到DynamicResConst.java中的对应资源

[详见 资源打包和使用说明.md](资源打包和使用说明.md)
## 4.初始化

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
详见DynamicInitJob.java

## 5.部分资源预加载

```java
IoThreadPool.getInstance().getThreadPoolExecutor().execute(() -> {
    if (DynamicResManager.getInstance().getLoadSoManager() != null) {
        DynamicResManager.getInstance().getLoadSoManager().loadSo(DynamicResConst.DEMO_SO, null);
    }
});
```
详见PreDynamicLoadJob.java

## 6.资源使用

1. 字体

   ```
   DynamicResManager.getInstance().setTypeface(mFontTv, DynamicResConst.TypeFace.FONT_FZ_RZ_BOLD);
   ```

2. 帧动画

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


详细请参考MainActivity.java

## 问题交流

- 如果你发现了bug或者有其他功能诉求，欢迎提issue。
- 如果想贡献代码，可以直接发起MR。

## 技术博客

[货拉拉 Android 动态资源管理系统原理与实践](https://juejin.cn/post/7113703128733581342)

## 作者

[货拉拉移动端技术团队](https://juejin.cn/user/1768489241815070)

## 许可证

采用Apache 2.0协议，详情参考[LICENSE](https://github.com/HuolalaTech/DynamicPlugin-android/blob/master/LICENSE)