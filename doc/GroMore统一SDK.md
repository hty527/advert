### 一、接入前的准备工作
* 1、开发者须具备Android开发基础及AndroidStudio编辑器的使用基础能力
* 2、到[GroMore后台][6]申请APP ID和广告位ID<br>
* 3、遇到问题请阅读[GroMore官方文档][1]<br>
* 4、更多原始广告平台支持请到[第三方广告SDK][2]下载<br>
* 5、配置、混淆、资源、权限等请参阅[官方文档][3]<br>

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
    dependencies {

        //GroMore统一SDK
        implementation 'com.github.hty527.advert:gromore:1.1.5.4'

        /**
         * 如果是Support环境
         */
        //implementation "com.android.support:appcompat-v7:28.0.0"
        //implementation 'com.android.support:localbroadcastmanager:28.0.0'
        //dex 分包，当minSdkVersion>=21时会出现找不到androidx.multidex.MultiDexApplication 解决办法
        //implementation 'com.android.support:multidex:1.0.3'
    
        /**
         * 如果是androidx环境
         * 需在工程的gradle.properties文件增加下列配置：
         * android.useAndroidX=true
         * android.enableJetifier=true
         */
        implementation 'androidx.appcompat:appcompat:1.0.2'
        implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.1.0'
        //dex 分包，当minSdkVersion>=21时会出现找不到androidx.multidex.MultiDexApplication  解决办法
        implementation 'androidx.multidex:multidex:2.0.1'
    }
```
##### 3、第三方聚合SDK及第三方广告SDK配置
* 3.1、复制模块app-gromore下libs_gromore到你的项目模块根目录下<br>
* 3.2、复制模块app-gromore下libs-adn到你的项目模块根目录下，可根据需要保留第三方聚合adapter和第三方广告SDK，更多广告SDK，请前往[官方下载][4]<br>
* 3.3、兼容Android10及以上设备建议集成模块app-gromore下libs目录中的oaid_sdk_1.0.25.aar<br>
* 3.4、完成文件拷贝后在模块build.gradle配置中添加下列配置<br>
* 下列配置示例仅适配了穿山甲、快手、优量汇三家广告，其它广告配置请参阅[GroMore官方文档][1]
```
    dependencies {
        //引入libs、libs_gromore、libs-adn目录下的所有.aar文件和.jar文件
        implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
        implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs_gromore')
        implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs-adn')

        /**
         * 如果集成了快手：
         */
        //Support：
        //implementation "com.android.support:design:28.0.0"
        //AndroidX：
        implementation "androidx.recyclerview:recyclerview:1.2.0"
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    }
```

* 请尽可能的申明下列权限，以免影响ECPM
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
[1]:https://www.csjplatform.com/union/media/union/download/detail?id=84&osType=android&locale=zh-CN "GroMore官方文档"
[2]:https://www.csjplatform.com/union/media/union/download?doc_sort=mediation "第三方广告SDK"
[3]:https://www.csjplatform.com/union/media/union/download/detail?id=84&docId=27211&osType=android "官方文档"
[4]:https://www.csjplatform.com/union/media/union/download?doc_sort=mediation "官方下载"
[5]:https://github.com/hty527/advert/wiki/GroMore统一SDK接入文档 "接入文档"
[6]:https://www.csjplatform.com/union/media/union "GroMore后台"
