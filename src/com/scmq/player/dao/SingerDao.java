package com.scmq.player.dao;

import com.scmq.player.model.Singer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 歌手数据访问对象层
 *
 * @author SCMQ
 */
public interface SingerDao {
    /**
     * 通过歌手名称和可选的平台id,来查找对应的歌手id.总是返回查询结果的第一个
     *
     * @param singer 歌手信息
     * @return 歌手id
     */
    Integer findIdByInfo(@Param("singer") Singer singer);

    /**
     * 查询所有歌手信息
     *
     * @return 歌手信息列表集合
     */
    List<Singer> findAll();

    /**
     * 通过id查询歌手信息
     *
     * @return 歌手信息
     */
    Singer findSingerById(@Param("id") Integer id);

    /**
     * 保存单个歌手信息
     *
     * @param singer 歌手信息
     * @return 若保存成功则返回1, 否则返回0
     */
    Integer saveOfSingle(@Param("singer") Singer singer);

    /**
     * 批量保存歌手信息
     *
     * @param singers 歌手信息List集合
     * @return 保存成功的数量
     */
    Integer save(@Param("singers") List<Singer> singers);

    /**
     * 修改歌手信息
     *
     * @param singer 歌手信息
     * @return 修改成功的数量
     */
    Integer update(@Param("singer") Singer singer);
}