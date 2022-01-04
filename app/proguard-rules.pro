# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#
#-------------------------------------------基本不用动区域----------------------------------------------
#
#
# -----------------------------基本 -----------------------------
#

# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项，
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify
# 屏蔽警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 #优化时允许访问并修改有修饰符的类和类的成员，这可以提高优化步骤的结果。
# 比如，当内联一个公共的getter方法时，这也可能需要外地公共访问。
# 虽然java二进制规范不需要这个，要不然有的虚拟机处理这些代码会有问题。当有优化和使用-repackageclasses时才适用。
#指示语：不能用这个指令处理库中的代码，因为有的类和类成员没有设计成public ,而在api中可能变成public
-allowaccessmodification
#当有优化和使用-repackageclasses时才适用。
-repackageclasses ''
 # 混淆时记录日志(打印混淆的详细信息)
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
-verbose

#
# ----------------------------- 默认保留 -----------------------------
#
#----------------------------------------------------
# 保持哪些类不被混淆
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}## 保留support下的所有类及其内部类

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#表示不混淆上面声明的类，最后这两个类我们基本也用不上，是接入Google原生的一些服务时使用的。
#----------------------------------------------------

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**


#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
    native <methods>;
}


#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#表示不混淆Parcelable实现类中的CREATOR字段，
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 这指定了继承Serizalizable的类的如下成员不被移除混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留R下面的资源
#-keep class **.R$* {
# *;
#}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class com.kaadas.lock.utils.KeyConstants {
     public final static java.lang.String VERSION;
}

#
#----------------------------- WebView -----------------------------
#
#webView需要进行特殊处理
#-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
#   public *;
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
#    public boolean *(android.webkit.WebView, java.lang.String);
#}
#-keepclassmembers class * extends android.webkit.WebViewClient {
#    public void *(android.webkit.WebView, java.lang.String);
#}

#在app中与HTML5的JavaScript的交互进行特殊处理
#-keepclassmembers class com.ljd.example.JSInterface {
#    <methods>;
#}

#
#---------------------------------实体类---------------------------------
#--------(实体Model不能混淆，否则找不到对应的属性获取不到值)-----
#
-keep class com.kaadas.lock.bean.**{*;}
-keep class com.kaadas.lock.publiclibrary.bean.**{*;}
-keep class com.kaadas.lock.publiclibrary.ble.bean.**{*;}
-keep class com.kaadas.lock.publiclibrary.ble.responsebean.**{*;}
-keep class com.kaadas.lock.publiclibrary.http.postbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.http.result.**{*;}
-keep class com.kaadas.lock.publiclibrary.http.temp.postbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.http.temp.resultbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.mqtt.eventbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.mqtt.publishbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.mqtt.publishresultbean.**{*;}
-keep class com.kaadas.lock.publiclibrary.mqtt.PowerResultBean{*;}
-keep class com.kaadas.lock.publiclibrary.mqtt.PublishResult{*;}
-keep class com.kaadas.lock.publiclibrary.xm.bean.**{*;}
-keep class com.kaadas.lock.utils.greenDao.bean.**{*;}

#对含有反射类的处理
-keep class com.kaadas.lock.shulan.KeepAliveRuning {*;}
-keep class la.xiong.androidquick.tool.ReflectUtil {*;}
-keep class la.xiong.androidquick.tool.Utils {*;}
-keep class la.xiong.androidquick.ui.permission.EasyPermissions {*;}
-keep class com.zhouyou.http.cache.converter.** {*;}
-keep class com.kaadas.lock.publiclibrary.linphone.linphonenew.LinphoneService{
    private void initData();
}



# ----------------------------- 其他的 -----------------------------
#
# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


#
# ----------------------------- 第三方 -----------------------------
#

# ----------------------------------gson----------------------------
-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}
-keep class sun.misc.Unsafe { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# ----------------------------------gson----------------------------

# -----------------------------butterknife--------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
# -----------------------------butterknife--------------------------------

# -------------------------------Retrofit----------------------------------
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
#-keepattributes Signature
-keepattributes Exceptions
# -------------------------------Retrofit----------------------------------

# ------------------------------RxJava RxAndroid----------------------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# ------------------------------RxJava RxAndroid----------------------------

# --------------------------------------okhttp 3-----------------------------
-dontwarn okio.**

-keep class com.squareup.okhttp.* { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }

# --------------------------------------okhttp 3-----------------------------

# ----------------------------------glide-------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# ----------------------------------glide-------------------------------------

# -----------------------------------xm---------------------------------
-libraryjars libs/xm-codec.jar
-libraryjars libs/xm-support.jar
-keep class com.xmitech.** { *; }
-keep interface com.xmitech.** { *; }
-keep class com.yuv.display.** { *; }
-keep interface com.yuv.display.** { *; }
-keep class com.coremedia.iso.** { *; }
-keep interface com.coremedia.iso.** { *; }
-keep class com.googlecode.mp4parser.** { *; }
-keep interface com.googlecode.mp4parser.** { *; }
-keep class com.p2p.pppp_api.** { *; }
-keep interface com.p2p.pppp_api.** { *; }
-keep class com.xm.sdk.** { *; }
-keep interface com.xm.sdk.** { *; }
-libraryjars libs/commons-net-3.3.jar
-libraryjars libs/Native_Libs2.jar
# -----------------------------------xm---------------------------------

# ---------------greendao---------------------
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties{*;}
# If you DO use SQLCipher:
-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }
#-keep class net.sqlcipher.database.**{*;}
#-keep public interface net.sqlcipher.database.**
#-dontwarn net.sqlcipher.database.**
-keep,includedescriptorclasses class net.sqlcipher.** { *; }
-keep,includedescriptorclasses interface net.sqlcipher.** { *; }
# ---------------greendao---------------------

# ---------------linphone---------------------
-dontwarn org.linphone.**
-keep class org.linphone.**{*;}
-keep interface org.linphone.**{*;}
# ---------------linphone---------------------

# ----------------hisilicon-----------------
-dontwarn com.hisilicon.**
-keep class com.hisilicon.**{*;}
# ----------------hisilicon-----------------

#------------------mimi---------------------------
-dontwarn net.sdvn.cmapi.**
-keep class net.sdvn.cmapi.**{*;}
#------------------mimi---------------------------

# ---------------eventbus---------------------
# eventbus 正式版org
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#eventbus 测试版de
-keepclassmembers class * {
    @de.greenrobot.event.Subscribe <methods>;
}
-keep enum de.greenrobot.event.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

-keepclassmembers class * {
    public void onEvent*(**);
}
# ---------------eventbus---------------------

# ----------------getui-----------------------
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
# ----------------getui-----------------------

# ----------------huawei push-----------------
-keepattributes InnerClasses
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
# ----------------huawei push-----------------


# ----------------meizu push------------------
-keep class com.meizu.** { *; }
-dontwarn com.meizu.**
# ----------------meizu push------------------


# ----------------oppo push------------------
-keep class com.heytap.** { *; }
-keep class com.mcs.aidl.** { *; }
-dontwarn com.heytap.**
-dontwarn com.mcs.aidl.**
# ----------------oppo push------------------


# ----------------vivo push------------------
-keep class com.vivo.push.** { *; }
-dontwarn com.vivo.push.**
# ----------------vivo push------------------


# ----------------xiaomi push------------------
-dontwarn com.xiaomi.**
-keep class com.xiaomi.** { *; }
# ----------------xiaomi push------------------


# ----------------STP push------------------
-dontwarn com.igexin.assist.control.**
-keep class com.igexin.assist.control.** { *; }
-dontwarn com.smartisan.sdk.**
-keep class com.smartisan.sdk.** { *; }
# ----------------STP push------------------


# ----------------ShareSDK------------------
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class com.mob.**{*;}
-keep class com.bytedance.**{*;}
-dontwarn cn.sharesdk.**
-dontwarn com.sina.**
-dontwarn com.mob.**
# ----------------ShareSDK------------------

# ----------------CymChad:BaseRecyclerViewAdapterHelper------------------
-keep public class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder
-keepclassmembers  class **$** extends com.chad.library.adapter.base.viewholder.BaseViewHolder {
     <init>(...);
}
# ----------------CymChad:BaseRecyclerViewAdapterHelper------------------

# -----------------eclipse mqtt--------------------------------------------------
-keep class org.eclipse.paho.android.service.** { *; }
-keep class org.eclipse.paho.client.mqttv3.** { *; }
# -----------------eclipse mqtt--------------------------------------------------