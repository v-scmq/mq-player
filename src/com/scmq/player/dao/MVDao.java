package com.scmq.player.dao;

import com.scmq.player.model.Media;
import com.scmq.player.model.MV;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MVDao {
    /**
     * 查询所有视频
     *
     * @param types 媒体类型
     * @return Video类型的List集合
     */
    List<MV> findAll(@Param("mediaTypes") Media.VideoType[] types);

    /**
     * 批量保存视频信息到数据表中
     *
     * @param list Video类型的List集合
     * @return 保存成功的数量
     */
    int save(@Param("mvList") List<MV> list);
}
