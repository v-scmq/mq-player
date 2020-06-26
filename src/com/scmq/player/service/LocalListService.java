package com.scmq.player.service;

import com.scmq.player.dao.LocalListDao;
import com.scmq.player.model.LocalList;
import com.scmq.player.model.Media.AudioType;
import com.scmq.player.model.Music;
import com.scmq.player.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 本地音乐列表业务
 *
 * @author SCMQ
 */
@Service("localListService")
public class LocalListService {
	@Autowired
	private LocalListDao dao;
	@Autowired
	private MusicService musicService;

	/**
	 * 查询本地音乐列表(数据库表中第一个)
	 *
	 * @return 本地音乐列表对象
	 */
	public LocalList findLocalListOfFirst() {
		// 查询表中第一个id
		Integer id = dao.findLocalListIdOfFirst();
		// 获取这个id对应的媒体
		return id == null ? null : dao.findLocalListById(id, AudioType.values());
	}

	/**
	 * 模糊搜索本地音乐,这将匹配音乐的名称、歌手名称、专辑名称中任意一个.
	 *
	 * @param id
	 *            本地音乐列表id
	 * @param key
	 *            搜索关键词
	 * @return 返回关键词所匹配的所有本地音乐信息
	 */
	public LocalList findByInfo(Integer id, String key) {
		if (id == null) {
			return null;
		}
		key = new StringBuilder().append('%').append(key).append('%').toString();
		return dao.findByInfo(id, key, AudioType.values());
	}

	/**
	 * 保存本地音乐列表到数据库,该方法开启一个事务,只有本地音乐列表媒体项数据保存成功才算成功
	 *
	 * @param localList
	 *            本地播放列表对象
	 * @return 保存成功的数量
	 */
	@Transactional
	public boolean saveLocalList(LocalList localList) {
		// 获取待保存音乐信息List集合
		List<Music> list = localList == null ? null : localList.getMusics();
		// 如果本地列表对象中没有任何音乐信息,则返回false
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 如果本地列表id为null
		if (localList.getId() == null) {
			// 尝试从数据库查询以获得id
			Integer id = dao.findLocalListIdOfFirst();
			// 若未查询到记录
			if (id == null) {
				// 保存新的本地音乐列表以获得id; 设置创建时间
				localList.setCreateTime(TimeUtil.currentTime());
				// 保存一个音乐列表,以获得本地音乐列表id(SQLite支持单行数据添加返回主键)
				if (dao.saveLocalList(localList) != 1) {
					// 如果没有保存成功,则返回false
					return false;
				}
			} else {
				// 设置id到本地列表对象中
				localList.setId(id);
			}
		}
		// 调用AudioDao接口的代理对象,批量保存所有音乐信息,以获得每个音乐信息在数据表中的id值(通过映射文件配置实现)
		// 如果保存成功,则批量保存音乐id到音乐id到local_media_item表中
		return musicService.save(list) && dao.saveLocalMediaItem(localList.getId(), list) == list.size();
	}

	/**
	 * 批量删除本地音乐列表媒体项,如果传入的id数量为0,则会执行全部删除
	 *
	 * @param mediaIds
	 *            本地音乐列表项媒体id(可变参数),如果没有则表示全部删除
	 * @return 删除成功的数量
	 */
	@Transactional
	public boolean deleteLocalMediaItem(Integer... mediaIds) {
		if (mediaIds == null || mediaIds.length == 0) {
			return false;
		}
		// ****还需要处理,因为只是从local_media_item表删除了记录,如果其他表没有引用这些记录,则删除
		return dao.deleteLocalMediaItem(mediaIds) == mediaIds.length;
	}
}
