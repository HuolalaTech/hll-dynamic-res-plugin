动态资源管理系统


使用步骤
1、创建DynamicPkgInfo类的常量实例，代表动态资源对象
2、在application的onCreate方法中，调用DynamicResManager类的init方法进行初始化
3、使用DynamicResManager.getInstance().setTypeface方法设置动态字体
4、使用DynamicResManager.getInstance().createFrameAnim方法设置动态帧动画
5、使用DynamicResManager.getInstance().load方法加载其他资源，这些资源需要自己处理


使用说明
1、提供了DynamicResManager类，作为整个系统的对外接口
2、提供了DynamicPkgInfo类，用来描述动态资源