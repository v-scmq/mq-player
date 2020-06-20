-- 媒体信息数据表
CREATE TABLE "media"
(
    "id"             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 媒体ID
    "title"          TEXT, -- 音乐或MV标题
    "singer_id"      INTEGER, -- 音乐或MV的歌手ID
    "album_id"       INTEGER, -- 音乐专辑ID
    "year"           TEXT, -- 音乐发布年份
    "size"           TEXT, -- 音乐或MV文件大小
    "duration"       TEXT, -- 媒体播放时长
    "file_name"      TEXT, -- 媒体文件名
    "path"           TEXT, -- 媒体资源路径
    "cover"          TEXT, -- 音乐或MV的封面URL
    "format"         TEXT, -- 媒体格式
    "channels"       TEXT, -- 媒体音频声道
    "quality"        INTEGER, -- 媒体音质
    "sample_rate"    TEXT, -- 音频采样率
    "audio_bit_rate" TEXT, -- 音频比特率
    "genre"          TEXT, -- 音乐流派
    "mid"            TEXT, -- 音乐MID
    "vid"            TEXT, -- MV vid
    "platform"       TEXT, -- 所属音乐平台(本地音乐为NULL)
    CONSTRAINT "singer_id" FOREIGN KEY ("singer_id") REFERENCES "singer" ("id"),
    CONSTRAINT "album_id" FOREIGN KEY ("album_id") REFERENCES "album" ("id")
);

-- 播放列表数据表
CREATE TABLE "play_list"
(
    "id"          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 播放列表ID
    "sort"        INTEGER, -- 排序方式
    "create_time" TEXT -- 创建时间
);

-- 本地音乐列表
CREATE TABLE "local_list"
(
    "id"          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 本地音乐列表ID
    "sort"        INTEGER, -- 排序方式
    "create_time" TEXT -- 创建时间
);

-- 用户信息数据表
CREATE TABLE "user"
(
    "qq"       TEXT NOT NULL, -- QQ号
    "name"     TEXT, -- QQ昵称
    "head_uri" TEXT, -- QQ头像URI
    PRIMARY KEY ("qq")
);

-- 资源下载信息数据表
CREATE TABLE "download"
(
    "media_id"    INTEGER NOT NULL, -- 媒体ID
    "create_time" TEXT, -- 下载任务创建时间
    "progress"    REAL(3, 2), -- 任务下载进度
    "state"       INTEGER, -- 任务状态[0就绪、1下载中、2下载暂停、3下载停止]
    PRIMARY KEY ("media_id"),
    CONSTRAINT "download_media_id" FOREIGN KEY ("media_id") REFERENCES "media" ("id")
);

-- 歌单信息数据表
CREATE TABLE "special"
(
    "id"          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 歌单ID
    "name"        TEXT, -- 歌单名称
    "cover"       TEXT, -- 歌单封面URI
    "introduce"   TEXT, -- 歌单简介
    "create_time" TEXT, -- 歌单创建时间
    "creator_id"  TEXT, -- 创建者ID
    "sort"        INTEGER, -- 排序方式
    "mid"         TEXT, -- 歌单MID
    "platform"    TEXT, -- 所属音乐平台(本地音乐为NULL)
    CONSTRAINT "creator" FOREIGN KEY ("creator_id") REFERENCES "user" ("qq")
);

-- 歌单项信息数据中间表
CREATE TABLE "special_item"
(
    "media_id"   INTEGER NOT NULL, -- 媒体ID
    "special_id" INTEGER, -- 歌单ID
    PRIMARY KEY ("media_id"),
    CONSTRAINT "special_item_media_id" FOREIGN KEY ("media_id") REFERENCES "media" ("id"),
    CONSTRAINT "special_id" FOREIGN KEY ("special_id") REFERENCES "special" ("id")
);

-- 播放媒体项数据信息中间表
CREATE TABLE "play_media_item"
(
    "media_id"     INTEGER NOT NULL, -- 媒体ID
    "play_list_id" INTEGER NOT NULL, --播放列表ID
    PRIMARY KEY ("play_list_id", "media_id"),
    CONSTRAINT "play_media_item" FOREIGN KEY ("media_id") REFERENCES "media" ("id"),
    CONSTRAINT "play_list_id" FOREIGN KEY ("play_list_id") REFERENCES "play_list" ("id")
);

-- 本地音乐列表项数据信息中间表
CREATE TABLE "local_media_item"
(
    "media_id"      INTEGER NOT NULL, -- 媒体ID
    "local_list_id" INTEGER NOT NULL, -- 本地音乐列表ID
    PRIMARY KEY ("media_id", "local_list_id"),
    CONSTRAINT "local_list_id" FOREIGN KEY ("local_list_id") REFERENCES "local_list" ("id"),
    CONSTRAINT "local_media_id" FOREIGN KEY ("media_id") REFERENCES "media" ("id")
);

-- 歌手信息数据表
CREATE TABLE "singer"
(
    "id"        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 歌手ID
    "name"      TEXT, -- 歌手名称
    "cover"     TEXT, -- 歌手封面URI
    "introduce" TEXT, -- 歌手简介
    "song_num"  INTEGER, -- 歌曲数量
    "mid"       TEXT, -- 歌手MID
    "platform"  TEXT -- 所属音乐平台(本地音乐为NULL)
);

-- 专辑信息数据表
CREATE TABLE "album"
(
    "id"        INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- 专辑ID
    "singer_id" INTEGER, -- 歌手ID
    "name"      TEXT, -- 专辑名称
    "cover"     TEXT, -- 专辑封面URI
    "introduce" TEXT, -- 专辑简介
    "year"      TEXT, -- 专辑发布年份
    "song_num"  INTEGER, -- 歌曲数量
    "mid"       TEXT, -- 专辑MID
    "platform"  TEXT, -- 所属音乐平台(本地音乐为NULL)
    CONSTRAINT "album_singer_id" FOREIGN KEY ("singer_id") REFERENCES "singer" ("id")
);
