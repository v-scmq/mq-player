## MQPlayer
MQ音乐是一个构建在Java和JavaFX平台之上的跨平台音乐播放器

### 项目技术栈
<div>
    <img src="https://img.shields.io/badge/Java-1.8-success.svg" alt>
    <img src="https://img.shields.io/badge/JavaFX-UI-success.svg" alt>
    <img src="https://img.shields.io/badge/Spring-4.3-blue.svg" alt>
    <img src="https://img.shields.io/badge/MyBatis-3.5-blue.svg" alt>
    <img src="https://img.shields.io/badge/VLCJ-4.2-green.svg" alt>
    <img src="https://img.shields.io/badge/SQLite-3.28-red.svg" alt>
</div>

<div>
<svg role="img" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><title id="simpleicons-github-dark-icon" lang="en">GitHub Dark icon</title><path fill="#7F8C8D" d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"></path></svg>
<a href="https://github.com/v-scmq/mq-player">GitHub地址</a>
<a href="https://gitee.com/scmq/mq-player/">Gitee地址</a>
</div>

### 特性
1. 支持音乐频谱
2. 高度支持自定义UI组件
3. 界面友好，支持皮肤切换，也可自定义样式
4. 跨平台，无需安装，仅需要Java运行时环境即可
5. 支持播放器解码器切换(JavaFX内置播放器和VLCJ播放器)

6. 良好的架构模式和代码风格
7. 提供支持主流的3个第三方音乐平台(仅作为学习目的，相关代码未公开)

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

![本地音乐](https://gitee.com/scmq/mq-player/raw/master/preview/1.png "本地音乐")

+ 播放详情

![播放详情](https://gitee.com/scmq/mq-player/raw/master/preview/2.png "背景虚化")

+ 歌手分类

![歌手分类](https://gitee.com/scmq/mq-player/raw/master/preview/3.png "歌手分类")

+ 歌手详情

![歌手详情](https://gitee.com/scmq/mq-player/raw/master/preview/4.png "歌手详情")

+ 歌手专辑

![歌手专辑](https://gitee.com/scmq/mq-player/raw/master/preview/5.png "歌手专辑")

+ 专辑详情

![专辑详情](https://gitee.com/scmq/mq-player/raw/master/preview/6.png "专辑详情")

+ 歌手MV

![歌手MV](https://gitee.com/scmq/mq-player/raw/master/preview/7.png "歌手MV列表")

+ 资源搜索

![歌曲搜索](https://gitee.com/scmq/mq-player/raw/master/preview/8.png "资源搜索")

+ 歌单分类

![歌单列表](https://gitee.com/scmq/mq-player/raw/master/preview/9.png "歌单列表")

+ MV分类

![MV列表](https://gitee.com/scmq/mq-player/raw/master/preview/10.png "MV列表")

+ 排行榜分类

![排行榜分类](https://gitee.com/scmq/mq-player/raw/master/preview/11.png "排行榜分类")
