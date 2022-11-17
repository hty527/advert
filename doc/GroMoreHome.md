### 一、接入前的准备工作
* 到GroMore后台申请app_id和广告位id<br>
* 复制demo模块app-gromore中app模块下的libs_gromore文件夹到你的项目中的app模块中<br>
* demo中libs目录下的oaid_sdk_1.0.25.aar为可选SDK，适配Android10及以上系统建议集成<br>
* 遇到问题请阅读[GroMore官方文档][1]<br>
* 更多平台广告支持[第三方广告SDK下载][2]<br>
* 配置、混淆、资源、权限等请参阅[官方文档][3]<br>

[1]:https://www.csjplatform.com/union/media/union/download/detail?id=84&osType=android&locale=zh-CN "GroMore官方文档"
[2]:https://www.csjplatform.com/union/media/union/download?doc_sort=mediation "第三方广告SDK下载"
[3]:https://www.csjplatform.com/union/media/union/download/detail?id=84&docId=27211&osType=android "官方文档"

### 二、AndroidStudio接入

#### 一、环境配置
##### 1、项目根build.gradle配置
```
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```
##### 2、模块build.gradle配置
```
    defaultConfig {
        minSdkVersion 17//如果使用了1.0.23及以上版本的oaid sdk，则最小版本为21
    }

    dependencies {
        //复制libs_gromore文件夹到app下后引入libs_gromore目录下的所有.aar文件和.jar文件
        implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs_gromore')
        //广告功能逻辑SDK
        implementation 'com.github.hty527.advert:gromore:1.1.2'

        /**
         * 如果是Support环境
         */
        //implementation "com.android.support:appcompat-v7:28.0.0"
        //implementation 'com.android.support:localbroadcastmanager:28.0.0'
        //集成包含快手广告需要添加下列1个依赖库
        //implementation "com.android.support:design:28.0.0"
        //dex 分包，当minSdkVersion>=21时会出现找不到androidx.multidex.MultiDexApplication 解决办法
        //implementation 'com.android.support:multidex:1.0.3'
    
        /**
         * 如果是androidx环境
         */
        implementation 'androidx.appcompat:appcompat:1.0.2'
        implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.1.0'
        //dex 分包，当minSdkVersion>=21时会出现找不到androidx.multidex.MultiDexApplication  解决办法
        implementation 'androidx.multidex:multidex:2.0.1'
        //集成包含快手广告需要添加下列2个依赖库
        implementation "androidx.recyclerview:recyclerview:1.2.0"
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    }
```
##### 3、复制app模块中libs_topon目录所有.aar文件到你的项目中并依赖，如需支持更多平台广告SDK，请点击[下载][4]
##### 4、权限
* 请尽可能的申明下列权限
```
    <!--GroMore SDK-BEGIN-通用 必要权限-->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />

    <!--必要权限，解决安全风险漏洞，发送和注册广播事件需要调用带有传递权限的接口-->
    <permission
        android:name="${applicationId}.openadsdk.permission.TT_PANGOLIN"
        android:protectionLevel="signature" />
    <uses-permission android:name="${applicationId}.openadsdk.permission.TT_PANGOLIN" />

    <!--可选权限-->
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.GET_TASKS"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />

    <!--可选，Mobrain SDK提供“获取地理位置权限”方式上报用户位置，两种方式均可不选，添加位置权限或参数将帮助投放定位广告-->
    <!--请注意：无论通过何种方式提供给穿山甲用户地理位置，均需向用户声明地理位置权限将应用于穿山甲广告投放，穿山甲不强制获取地理位置信息-->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <!-- 如果有视频相关的广告且使用textureView播放，请务必添加，否则黑屏 -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <!-- 高于Android 11的系统上，如果应用的 targetSdkVersion >= 30 ，推荐增加以下权限声明
       （SDK将通过此权限正常触发广告行为，并保证广告的正确投放。此权限需要在用户隐私文档中声明)-->
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
    <!--GroMore SDK-END-->
```
#### 二、广告展示、拉取、缓存、混淆等请阅读[接入文档][5]
[4]:https://www.csjplatform.com/union/media/union/download?doc_sort=mediation "下载"
[5]:https://github.com/hty527/advert/wiki/GroMoreWiki "接入文档"