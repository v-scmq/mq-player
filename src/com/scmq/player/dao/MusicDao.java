package com.scmq.player.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scmq.player.model.Music;
import com.scmq.player.model.Media.AudioType;

/**
 * @author SCMQ
 */
public interface MusicDao {
    /**
     * 查询所有音乐
     *
     * @param types 媒体类型
     * @return Audio类型的List集合
     */
    List<Music> findAll(@Param("mediaTypes") AudioType[] types);

    /**
     * 通过音乐信息来查找音乐id,如果是本地音乐,只需检查本地路径,因为存储本地音乐时总是可以保证本地路径是可知的;
     * 若是非本地音乐,则需要检查音乐名称和平台id,因为网络音乐平台的信息在存储时通常不会有URI,即使有效URI,也不存储.
     *
     * @param music 音乐信息
     * @return 音乐id, 总是返回查询结果的第一个
     */
    Integer findIdByInfo(@Param("music") Music music);

    /**
     * 批量保存音乐信息到数据表中
     *
     * @param list 音乐信息List集合
     * @return 保存成功的数量
     */
    Integer save(@Param("musics") List<Music> list);

    /**
     * 批量修改音乐信息
     *
     * @param list 音乐信息List集合
     * @return 修改成功的数量
     */
    Integer update(@Param("musics") List<Music> list);
}
