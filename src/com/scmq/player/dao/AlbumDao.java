package com.scmq.player.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.scmq.player.model.Album;

/**
 * 专辑信息数据访问对象层
 *
 * @author SCMQ
 */
public interface AlbumDao {
    /**
     * 通过专辑信息查找专辑id.<br>
     * 若是本地歌曲专辑,则通过{@link Album#getName()}查找;
     * 否则通过{@link Album#getMid()}}和{@link Album#getPlatform()}查找
     *
     * @param album 专辑信息对象
     * @return 专辑id
     */
    Integer findIdByInfo(@Param("album") Album album);

    /**
     * 查询所有专辑信息
     *
     * @return 专辑信息List集合
     */
    List<Album> findAll();

    /**
     * 通过id查询专辑信息
     *
     * @return 专辑信息对象
     */
    Album findAlbumById(@Param("id") Integer id);

    /**
     * 保存单个专辑信息
     *
     * @param album 专辑信息
     * @return 若保存成功则返回1, 否则返回0
     */
    Integer saveOfSingle(@Param("album") Album album);

    /**
     * 批量保存专辑信息
     *
     * @param albums 多个专辑信息
     * @return 保存成功的数量
     */
    Integer save(@Param("albums") List<Album> albums);

    /**
     * 修改专辑信息
     *
     * @param album 专辑信息
     * @return 修改成功的数量
     */
    Integer update(@Param("album") Album album);
}
