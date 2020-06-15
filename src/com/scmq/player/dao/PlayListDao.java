package com.scmq.player.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scmq.player.model.Media.AudioType;
import com.scmq.player.model.Music;
import com.scmq.player.model.PlayList;

/**
 * 播放队列数据操作接口
 */
public interface PlayListDao {
    /**
     * 查询播放列表的id(数据库表中最后一个)
     *
     * @return 第一个音乐列表id
     */
    Integer findPlayListIdOfLast();

    /**
     * 通过播放列表id查询播放队列
     *
     * @param id    播放列表id
     * @param types 音频类型 {@code AudioType}
     * @return 播放列表对象
     */
    PlayList findPlayListById(@Param("id") Integer id, @Param("mediaTypes") AudioType[] types);

    /**
     * 保存播放列表到数据库
     *
     * @param playList 播放列表对象
     * @return 保存成功的数量
     */
    Integer savePlayList(@Param("playList") PlayList playList);

    /**
     * 批量保存播放列表id和和音乐id到播放列表与媒体表的中间表local_media_item
     *
     * @param id   播放列表id
     * @param list 音乐信息的List集合
     * @return 保存成功的数量
     */
    Integer savePlayMediaItem(@Param("id") Integer id, @Param("list") List<Music> list);

    /**
     * 批量删除播放列表媒体项,如果传入的id数量为0,则会执行全部删除
     *
     * @param mediaIds 播放列表项媒体id的{@code int[]}
     * @return 删除成功的数量
     */
    Integer deletePlayMediaItem(@Param("mediaIds") Integer[] mediaIds);
}
