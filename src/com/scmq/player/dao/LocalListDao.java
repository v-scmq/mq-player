package com.scmq.player.dao;

import com.scmq.player.model.Music;
import com.scmq.player.model.LocalList;
import com.scmq.player.model.Media.AudioType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 本地音乐列表数据操作层接口
 *
 * @author SCMQ
 */
public interface LocalListDao {
	/**
	 * 查询本地音乐列表的id(数据库表中第一个)
	 *
	 * @return 第一个音乐列表id
	 */
	Integer findLocalListIdOfFirst();

	/**
	 * 通过本地音乐列表id和音频类型查询本地音乐列表
	 *
	 * @param id
	 *            本地音乐列表id
	 * @param types
	 *            音乐类型
	 * @return 本地音乐列表对象
	 */
	LocalList findLocalListById(@Param("id") int id, @Param("mediaTypes") AudioType[] types);

	/**
	 * 通过关键词模糊查找本地音乐,查找依据是歌曲名称、歌手名称、专辑名称. 关于模糊搜索时,字符串的解决方案有2种:
	 * <ol>
	 * <li>column like '%${key}%' 的方式(之前采用这种方式,会出现异常,比如输入了英文的单引号就会导致错误)</li>
	 * <li>column like #{key} 的方式,但必须先处理key变量的值为'%实际内容%'(现在默认)</li>
	 * </ol>
	 *
	 * @param id
	 *            本地音乐列表id
	 * @param key
	 *            搜索关键词
	 * @return 关键词相关的本地音乐记录
	 * @see com.scmq.player.service.LocalListService#findByInfo(Integer, String)
	 */
	LocalList findByInfo(@Param("id") Integer id, @Param("key") String key, @Param("mediaTypes") AudioType[] types);

	/**
	 * 保存本地音乐列表到数据库
	 *
	 * @param localList
	 *            本地音乐列表对象
	 * @return 保存成功的数量
	 */
	Integer saveLocalList(@Param("localList") LocalList localList);

	/**
	 * 批量保存本地音乐列表id和和音乐id到本地音乐列表与媒体表的中间表local_media_item
	 *
	 * @param localListId
	 *            本地音乐列表id
	 * @param musicList
	 *            音乐信息的List集合
	 * @return 保存成功的数量
	 */
	Integer saveLocalMediaItem(@Param("localListId") int localListId, @Param("musicList") List<Music> musicList);

	/**
	 * 批量删除本地音乐列表媒体项,如果传入的id数量为0,则会执行全部删除
	 *
	 * @param mediaIds
	 *            本地音乐列表项媒体id的{@code int[]}
	 * @return 删除成功的数量
	 */
	Integer deleteLocalMediaItem(@Param("mediaIds") Integer[] mediaIds);
}
