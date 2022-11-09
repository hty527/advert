
-optimizationpasses 5
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#混淆时是否记录日志
-verbose
#忽略警告
-ignorewarnings
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes EnclosingMethod
-dontoptimize
# 混淆时不使用大小写混合，混淆后的类名为小写
# windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类
# 默认跳过，有些情况下编写的代码与类库中的类在同一个包下，并且持有包中内容的引用，此时就需要加入此条声明
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 不做预检验，preverify是proguard的四个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify
# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
#-verbose

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep public class * extends android.app.Activity{
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application{
	public <fields>;
	public <methods>;
}

#SDK所有R文件不参与混淆
-keep public class com.platform.lib.R$*{
    public static final int *;
}

#保留出口及出口内的部分方法不被混淆
#保留AdvertManager类的所有公开方法这个
#-keep class com.topon.advert.manager.AdvertManager {*;}
-keep class com.platform.lib.manager.PlatformManager {
    public *;
}
-keep class com.platform.lib.manager.TableScreenManager {
    public *;
}
-keep class com.platform.lib.manager.PlayManager {
    public *;
}
-keep class com.platform.lib.constants.AdConstance {*;}
-keep class com.platform.lib.bean.Result {
    public *;
}
-keep class com.platform.lib.utils.Logger {
    public *;
}
-keep class com.platform.lib.listener.** {*;}
#GroMore保持竞价逻辑不被混淆
-keep class com.platform.lib.adn.** {*;}
#保留ExpressView类的requst及destroy方法
-keep class com.platform.lib.widget.ExpressView {
    public void requst();
    public void destroy();
    public void addErrorView(int, java.lang.String, java.lang.String);
}
-keep class com.platform.lib.widget.SplashView {
    public *;
}
#将所有混淆的类放到指定目录下
-repackageclasses com.platform.libs
