## MQPlayer
MQ音乐是一个构建在Java和JavaFX平台之上的跨平台音乐播放器

### 项目技术栈
<div style="display:flex;align-items:center;justify-content:center;">
    <img src="https://img.shields.io/badge/Java-1.8-success.svg" alt>
    <img src="https://img.shields.io/badge/JavaFX-UI-success.svg" alt>
    <img src="https://img.shields.io/badge/Spring-4.3-blue.svg" alt>
    <img src="https://img.shields.io/badge/MyBatis-3.5-blue.svg" alt>
    <img src="https://img.shields.io/badge/VLCJ-4.2-green.svg" alt>
    <img src="https://img.shields.io/badge/SQLite-3.28-red.svg" alt>
</div>

### 特性
1. 支持音乐频谱
2. 高度支持自定义UI组件
3. 界面友好，支持皮肤切换，也可自定义样式
4. 跨平台，无需安装，仅需要Java运行时环境即可
5. 支持播放器解码器切换(JavaFX内置播放器和VLCJ播放器)

6. 良好的架构模式和代码风格
7. 提供支持主流的3个第三方音乐平台(仅作为学习目的，也未公开)

### 架构
本软件采用主流的三层架构和MVC模式

|  编号    | 包/资源 | 明细 |
| ------   | ------ | ----- |
|  1   | com.scmq.player.app        | 主程序入口 |
|  2   | com.scmq.player.controller | 所有控制器 |
|  3   | com.scmq.player.core       | 播放器核心实现 |
|  4   | com.scmq.player.dao        | mapper(DAO)接口 |
|  5   | com.scmq.player.io         | IO操作和歌词支持的类 |
|  6   | com.scmq.player.model      | 数据模型类 |
|  7   | com.scmq.player.net        | 提供网络支持的类 |
|  8   | com.scmq.player.service    | 所有业务类 |
|  9   | com.scmq.player.util       | 所有工具类 |
|  10  | com.scmq.player.view       | 视图类 |
|  11  | com.scmq.view.control      | 自定义UI组件 |
|  13  | res/icon/player            | 播放器专用图标 |
|  12  | res/icon                   | 图标 |
|  14  | res/mapper                 | mapper映射文件 |
|  15  | res/style                  | UI样式 |

***
### 效果预览
+ 本地音乐

![本地音乐](https://cq-download-ipv6.ftn.qq.com/ftn_handler/1b0bac8c61a3354a371fd95172e1575aaed8c9a1be49efcce61b2eda4fa618699542144864a52de28e1eaf75787d7fb71ca2d147705b1df35cb328b36f46e46b/?fname=*.png&pictype=scaled&size=1024*768 "本地音乐")

+ 播放详情

![播放详情](https://cq-download-ipv6.ftn.qq.com/ftn_handler/7f2a419bfafdc500bf1dd26f8a0ab3376828bbd9c7e813be5b93f9c6f5ab410296b571abfcb4d7376119d08812fcff6ca3d83a827c218f2c47dc357126e361fe/?fname=*.png&pictype=scaled&size=1024*7 "背景虚化")

+ 歌手分类

![歌手分类](https://cq-download-ipv6.ftn.qq.com/ftn_handler/c17cbc2989f8dc6da552b94c072461f231dee37038eac7559217cfcf187ec244db4372fc153b6aa44a98a295d405e7b8f58b256b217466636a61a36d85698a4a/?fname=*.png&pictype=scaled&size=1024*768 "歌手分类")

+ 歌手详情

![歌手详情](https://cq-download-ipv6.ftn.qq.com/ftn_handler/58c72f6c90d2724817619ad9994cbe67dd5a9e71010166b1b025a7440439524ec30ef427760ffb77629ca6dad7d2be7b5beda950d9f5a5a5cfa0acc9ebdd1b14/?fname=*.png&pictype=scaled&size=1024*7 "歌手详情")

+ 歌手专辑

![歌手专辑](https://cq-download-ipv6.ftn.qq.com/ftn_handler/8c70a79cd2d0d4f939849fc40325459651f8ac7222f3c627b9f3f317af18e245261c5b0e2818ebc45a10cc9860447835bf4fbf5efcb806a6faf7fff8c43f09a9/?fname=*.png&pictype=scaled&size=1024*768 "歌手专辑")

+ 专辑详情

![专辑详情](https://cq-download-ipv6.ftn.qq.com/ftn_handler/c6a5824d1e787c1fd3a7ec4f6ebeeff1e8c0292a3711a255472fa323614798ab27b831f659451c464b1e931a2b344594491005b377387b523bc92abf130195c3/?fname=*.png&pictype=scaled&size=1024*768 "专辑详情")

+ 歌手MV列表

![歌手MV列表](https://cq-download-ipv6.ftn.qq.com/ftn_handler/afb9bf2adb6513a7744a3071e71dde99fcb9e0754251c67818ed04c7c38653f23a7348dad93b40bccd24657ea7729a6fe533a97ef095897ad7563040f5e58fb9/?fname=*.png&pictype=scaled&size=1024*768 "歌手MV列表")

+ 歌曲搜索

![歌曲搜索](https://cq-download-ipv6.ftn.qq.com/ftn_handler/3117aec69546d82453f26bc421b8367f15c5587f63688c9dd46067f7aeeec219ce734124f35e01ebee760e728032ec1c8ccca83095e1c411a3360f9683ce1e52/?fname=*.png&pictype=scaled&size=1024*768 "歌曲搜索")